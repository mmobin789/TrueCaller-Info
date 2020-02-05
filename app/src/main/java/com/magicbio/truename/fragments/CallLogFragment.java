package com.magicbio.truename.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.adapters.CallLogsAdapter;
import com.magicbio.truename.adapters.HotNumbersAdapter;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.utils.CommonAnimationUtils;
import com.magicbio.truename.utils.SimpleCountDownTimer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement an interface
 * to handle interaction events.
 * Use the {@link CallLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<CallLogModel> list;
    private RecyclerView recyclerView;
    private boolean adShown;
    private CallLogsAdapter callLogsAdapter;

    public CallLogFragment() {
        // Required empty public constructor
    }

    public void search(String newText) {

        callLogsAdapter.search(newText.toLowerCase(), null);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CallLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CallLogFragment newInstance(String param1, String param2) {
        CallLogFragment fragment = new CallLogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call_log, container, false);
        init(v);
        return v;
    }


    private void init(View v) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "giving read call log permission");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    1);
            Log.i(TAG, "giving read call log permission 2");
        }
        recyclerView = v.findViewById(R.id.recycler_View);
        RecyclerView hotNumbers = v.findViewById(R.id.hotNumbers);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        final TextView tvLoading = v.findViewById(R.id.tvLoading);
        CommonAnimationUtils.applyFadeInFadeOut(tvLoading);


        SimpleCountDownTimer simpleCountDownTimer = new SimpleCountDownTimer(0, 1, new SimpleCountDownTimer.OnCountDownListener() {
            @Override
            public void onCountDownActive(@NotNull String time) {
                list = getCallDetails(recyclerView.getContext());
            }

            @Override
            public void onCountDownFinished() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tvLoading.setVisibility(View.GONE);
                        tvLoading.clearAnimation();
                        callLogsAdapter = new CallLogsAdapter(list, recyclerView.getContext());
                        recyclerView.setAdapter(callLogsAdapter);

                        if (!adShown) {
                            adShown = true;
                            callLogsAdapter.showAd();
                        }
                    }
                });
            }
        }, 1);

        simpleCountDownTimer.runOnBackgroundThread();

        simpleCountDownTimer.start(false);


        HotNumbersAdapter itemListDataAdapter = new HotNumbersAdapter(getHotCalls(recyclerView.getContext()), getContext());

        hotNumbers.setHasFixedSize(true);
        hotNumbers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotNumbers.setAdapter(itemListDataAdapter);
        // hotNumbers.setRecycledViewPool(viewPool);

    }

    private List<CallLogModel> getCallDetails(Context context) {
        StringBuilder sb = new StringBuilder();

        List<CallLogModel> callLogModelList = new ArrayList<>();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission")
        Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, "DATE desc");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            HashMap rowDataCall = new HashMap<String, String>();
            CallLogModel call = new CallLogModel();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDayTime = new Date(Long.valueOf(callDate)).toString();
            String name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String image = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID));
            // long timestamp = convertDateToTimestamp(callDayTime);
            String callDuration = managedCursor.getString(duration);
            String simn = managedCursor.getString(sim);
            String dir = null;
            String sid = managedCursor.getString(id);
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
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
                    + " \nname :--- " + name);
            sb.append("\n----------------------------------");

            call.setCallType(dir);
            call.setCallDate(callDate);
            call.setPhNumber(phNumber);
            call.setCallDayTime(callDayTime);
            call.setSim(simn);
            call.setName(name);
            call.set_Id(sid);
            call.setImage(image);
            int hours = Integer.valueOf(callDuration) / 3600;
            int minutes = (Integer.valueOf(callDuration) % 3600) / 60;
            int seconds = Integer.valueOf(callDuration) % 60;
            call.setCallDuration(String.format("%02d:%02d", minutes, seconds));

            // Uri allCalls = Uri.parse("content://call_log/calls");
            // Cursor c = ((MainActivity)getActivity()).managedQuery(allCalls, null, null, null, null);
            //String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            //Log.d("sim",id);
            callLogModelList.add(call);


        }
        managedCursor.close();
        //System.out.println(sb);
        return callLogModelList;
    }

    private List<CallLogModel> getHotCalls(Context context) {
        StringBuilder sb = new StringBuilder();

        List<CallLogModel> callLogModelList = new ArrayList<>(10);
        Uri contacts = CallLog.Calls.CONTENT_URI;
        //   String[] projection = {"top 10", "number", "MAX(date) as date", "name", "type"};
        @SuppressLint("MissingPermission")
        Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, "DATE desc limit 10");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            //  HashMap rowDataCall = new HashMap<String, String>();
            CallLogModel call = new CallLogModel();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDayTime = new Date(Long.valueOf(callDate)).toString();
            String name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            // long timestamp = convertDateToTimestamp(callDayTime);
            call.setCallDate(callDate);
            call.setPhNumber(phNumber);
            call.setCallType(callType);
            call.setCallDayTime(callDayTime);
            call.setName(name);


            // Uri allCalls = Uri.parse("content://call_log/calls");
            // Cursor c = ((MainActivity)getActivity()).managedQuery(allCalls, null, null, null, null);
            //String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            //Log.d("sim",id);
            callLogModelList.add(call);


        }
        managedCursor.close();
        //System.out.println(sb);
        return callLogModelList;
    }

 /*   private CallLogModel getCalllast(Context context) {
        StringBuffer sb = new StringBuffer();
        String selection = CallLog.Calls.NUMBER + " =03066855423";
        CallLogModel callLogModelList = new CallLogModel();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission")
        Cursor managedCursor = context.getContentResolver().query(contacts, null, selection, null, "DATE desc");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

          //  HashMap rowDataCall = new HashMap<String, String>();
            CallLogModel call = new CallLogModel();

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
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
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
                    + " \nname :--- " + name);
            sb.append("\n----------------------------------");

            call.setCallType(dir);
            call.setCallDate(callDate);
            call.setPhNumber(phNumber);
            call.setCallDayTime(callDayTime);
            call.setSim(simn);
            call.setName(name);
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
    }*/



/*    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            list = getCallDetails(getContext());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            progressDoalog.dismiss();
            callLogsAdapter = new CallLogsAdapter(list, getContext());
            recyclerView.setAdapter(callLogsAdapter);
            if (!adShown)
                callLogsAdapter.showAd();
            adShown = true;
            // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/


}
