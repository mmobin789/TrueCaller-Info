package com.magicbio.truename.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.RecordModel;
import com.magicbio.truename.adapters.SMSAdapter;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.models.Sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SMSAdapter smsAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<Sms> smsList = new ArrayList<>();
    ProgressDialog progressDoalog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        // List<Sms> sms=getAllSms();
        // Toast.makeText(getContext(),"size"+sms.size(),Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "giving read call log permission");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_SMS},
                    1);
            Log.i(TAG, "giving read call log permission 2");
        } else {
            init(v);
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void init(View v) {
        progressDoalog = new ProgressDialog(getContext());
        progressDoalog.setMessage("Please Wait.....");
        progressDoalog.setCancelable(false);
        progressDoalog.setIndeterminate(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        recyclerView = v.findViewById(R.id.recycler_View);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        new LongOperation().execute("");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                smsList = getAllSms();
                smsAdapter = new SMSAdapter(smsList, getContext());
                recyclerView.setAdapter(smsAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        List<RecordModel> re = RecordModel.getAll();
        if (re.size() != 0) {
            Toast.makeText(getContext(), re.get(0).getRid(), Toast.LENGTH_LONG).show();
        }

//        Timer myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(getActivity()!=null)
//                {
//                    new LongOperation().execute("");
//                }
//            }
//
//        }, 0, 3000);

    }

    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        // Uri message = Uri.parse("content://sms/conversations");
        ContentResolver cr = getActivity().getContentResolver();
        //Telephony.Sms.PERSON;
        String[] projection = {"thread_id", "MAX(date) as date", "COUNT(*) AS msg_count", "body", "address", "(COUNT(*)-SUM(read)) as unread"};

        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, projection, "thread_id) GROUP BY (thread_id", null, null);
        DatabaseUtils.dumpCursor(c);
        int totalSMS = c.getCount();
        try {


            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new Sms();
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("thread_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setReadState(c.getString(c.getColumnIndex("unread")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    if (c.getString(c.getColumnIndexOrThrow("msg_count")).contains("1")) {
                        objSms.setFolderName("inbox");
                    } else {
                        objSms.setFolderName("sent");
                    }

                    lstSms.add(objSms);
                    c.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return lstSms;
    }

    public List<Sms> getAllSms1() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/conversations");
        ContentResolver cr = getActivity().getContentResolver();
        //Telephony.Sms.PERSON;
        String[] projection = {"thread_id", "MAX(date) as date", "COUNT(*) AS msg_count", "body", "address", "sum(seen) as unread"};

        Cursor c = cr.query(message, null, null, null, null);
        DatabaseUtils.dumpCursor(c);
        int totalSMS = c.getCount();
        try {


            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

//                    objSms = new Sms();
//                    objSms.setId(c.getString(c.getColumnIndexOrThrow("thread_id")));
//                    objSms.setAddress(c.getString(c
//                            .getColumnIndexOrThrow("address")));
//                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
//                    objSms.setReadState(c.getString(c.getColumnIndex("msg_count")));
//                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
//                    if (c.getString(c.getColumnIndexOrThrow("msg_count")).contains("1")) {
//                        objSms.setFolderName("inbox");
//                    } else {
//                        objSms.setFolderName("sent");
//                    }
//
//                    lstSms.add(objSms);
//                    c.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return lstSms;
    }

    private CallLogModel getCalllast(Context context, String numbers) {
        StringBuffer sb = new StringBuffer();

        String selection = CallLog.Calls.NUMBER + " = " + numbers;

        String[] strNumber = {numbers};
        CallLogModel callLogModelList = new CallLogModel();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission")
        Cursor managedCursor = context.getContentResolver().query(contacts, null, CallLog.Calls.NUMBER + " = ? ", strNumber, "DATE desc");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            HashMap rowDataCall = new HashMap<String, String>();
            CallLogModel call = new CallLogModel();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDayTime = new Date(Long.valueOf(callDate)).toString();
            // long timestamp = convertDateToTimestamp(callDayTime);
            String callDuration = managedCursor.getString(duration);
            String simn = managedCursor.getString(sim);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                default:
                    dir = "OUTGOING";
                    break;

            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");

            call.setCallType(dir);
            call.setCallDate(callDate);
            call.setPhNumber(phNumber);
            call.setCallDayTime(callDayTime);
            call.setSim(simn);
            int hours = Integer.valueOf(callDuration) / 3600;
            int minutes = (Integer.valueOf(callDuration) % 3600) / 60;
            int seconds = Integer.valueOf(callDuration) % 60;
            call.setCallDuration(String.format("%02d:%02d", minutes, seconds));

            // Uri allCalls = Uri.parse("content://call_log/calls");
            // Cursor c = ((MainActivity)getActivity()).managedQuery(allCalls, null, null, null, null);
            //String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            //Log.d("sim",id);
            callLogModelList = call;


        }
        managedCursor.close();
        System.out.println(sb);
        return callLogModelList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //progressDoalog.show();
            smsList = getAllSms();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            progressDoalog.dismiss();
            smsAdapter = new SMSAdapter(smsList, getActivity());
            recyclerView.setAdapter(smsAdapter);
            // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            progressDoalog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
