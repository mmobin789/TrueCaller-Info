package com.magicbio.truename.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.adapters.CallLogsAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.models.CallLogModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement an interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CallLogFragment extends Fragment {
    private boolean adShown;
    private RecyclerView recyclerView;
    private CallLogsAdapter callLogsAdapter;
    private ProgressDialog progressDialog;

    public CallLogFragment() {
        // Required empty public constructor
    }

    public void search(String newText) {
        if (newText != null)
            callLogsAdapter.search(newText.toLowerCase(), null);
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
        // hotNumbers = v.findViewById(R.id.hotNumbers);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        //   HotNumbersAdapter itemListDataAdapter = new HotNumbersAdapter(getHotCalls(recyclerView.getContext()), getContext());

        // hotNumbers.setHasFixedSize(true);
        // hotNumbers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // hotNumbers.setAdapter(itemListDataAdapter);
        setCallLogsAdapter();

        // hotNumbers.setRecycledViewPool(viewPool);


    }


    private void setCallLogsAdapter() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait.....");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        AppAsyncWorker.fetchCallLog(new Function1<ArrayList<CallLogModel>, Unit>() {
            @Override
            public Unit invoke(ArrayList<CallLogModel> callLogModels) {
                setCallLogsAdapter(callLogModels);
                progressDialog.dismiss();
                return Unit.INSTANCE;
            }
        });


    }

    private void setCallLogsAdapter(List<CallLogModel> list) {
        callLogsAdapter = new CallLogsAdapter(list, recyclerView.getContext());
        recyclerView.setAdapter(callLogsAdapter);

        if (!adShown) {
            adShown = true;
            callLogsAdapter.showAd();
        }

    }
    /*private List<CallLogModel> getCallDetails(Context context) {
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

          //  HashMap rowDataCall = new HashMap<String, String>();
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
    }*/

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


}
