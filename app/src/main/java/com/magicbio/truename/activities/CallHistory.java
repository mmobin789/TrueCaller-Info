package com.magicbio.truename.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.magicbio.truename.R;
import com.magicbio.truename.adapters.CallHistoryAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.services.InComingCallPop;
import com.magicbio.truename.utils.ContactUtils;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;

public class CallHistory extends AppCompatActivity implements OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {
    TextView txtName;
    ImageView btnMessage, btnCall, btnInvite, btnSave, btnLocation;
    RecyclerView recyclerView;
    ArrayList<String> numbers;
    /*   private TextView locationText;
       private TextView activityText;
       private TextView geofenceText;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        txtName = findViewById(R.id.txtName);
        btnMessage = findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);
        btnInvite = findViewById(R.id.btnInvite);
        btnSave = findViewById(R.id.btnSave);
        btnLocation = findViewById(R.id.btnLocation);
        ImageView btnBlock = findViewById(R.id.btnBlock);
        ImageView btnWA = findViewById(R.id.btnwa);
      /*  locationText = findViewById(R.id.sample);
        geofenceText = findViewById(R.id.sample);
        activityText = findViewById(R.id.sample);*/
        numbers = getIntent().getStringArrayListExtra("numbers");
        String name = getIntent().getStringExtra("name");
        txtName.setText(name);
        String number = numbers.get(0);
        setupClick(number);
        init();


        if (ContactUtils.isContactName(name)) {
            btnSave.setVisibility(View.GONE);
            btnBlock.setVisibility(View.VISIBLE);
        } else {
            btnSave.setVisibility(View.VISIBLE);
            btnBlock.setVisibility(View.GONE);
            btnWA.setVisibility(View.GONE);
            txtName.setText(number);
        }
    }


    private void setupClick(String number) {

        findViewById(R.id.btnwa).setOnClickListener(v -> ContactUtils.openWhatsAppChat(number));

        btnMessage.setOnClickListener(v -> ContactUtils.openSmsApp(number));
        btnCall.setOnClickListener(v -> ContactUtils.openDialer(number));
        btnInvite.setOnClickListener(v -> {
//                        Uri uri = Uri.parse("smsto:"+number);
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("sms_body", "Invite Text");
//                        startActivity(intent);

            final Intent i = InComingCallPop.getIntent(CallHistory.this);
            i.putExtra("number", number);
            i.putExtra("ptype", 2);
            startService(i);

        });
        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
            startActivity(intent);
        });
        btnLocation.setOnClickListener(v -> ContactUtils.shareLocationOnSms(number, txtName.getText().toString()));

    }

    public void init() {
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CallHistory.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //  recyclerView.setAdapter(new CallHistoryAdapter(getCallDetails(CallDetails.this, getIntent().getStringExtra("number"))));

        AppAsyncWorker.fetchCallHistory(numbers, result -> recyclerView.setAdapter(new CallHistoryAdapter(result)));


    }

    /*private List<CallLogModel> getCallDetails(Context context, String numbers) {
        StringBuffer sb = new StringBuffer();

        List<CallLogModel> callLogModelList = new ArrayList<>();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission")
        Cursor managedCursor = context.getContentResolver().query(contacts, null, "number=?", new String[]{numbers}, "DATE desc");
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
            String name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
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
            // Cursor c = ((MainActivity)CallDetails.this).managedQuery(allCalls, null, null, null, null);
            //String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            //Log.d("sim",id);
            callLogModelList.add(call);


        }
        managedCursor.close();
        System.out.println(sb);
        return callLogModelList;
    }*/



  /*  private void stopLocation() {
        SmartLocation.with(this).location().stop();
        locationText.setText("Location stopped!");

        SmartLocation.with(this).activity().stop();
        activityText.setText("Activity Recognition stopped!");

        SmartLocation.with(this).geofencing().stop();
        geofenceText.setText("Geofencing stopped!");
    }*/

   /* private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            locationText.setText(text);

            // We are going to get the address for the current position
            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\n[Reverse Geocoding] ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        locationText.setText(builder.toString());
                    }
                }
            });
        } else {
            locationText.setText("Null location");
        }
    }*/

 /*   private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        } else {
            activityText.setText("Null activity");
        }
    }*/

   /* private void showGeofence(Geofence geofence, int transitionType) {
        if (geofence != null) {
            geofenceText.setText("Transition " + getTransitionNameFromType(transitionType) + " for Geofence with id = " + geofence.getRequestId());
        } else {
            geofenceText.setText("Null geofence");
        }
    }*/

    @Override
    public void onLocationUpdated(Location location) {
        //  showLocation(location);
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        //showActivity(detectedActivity);
    }

    @Override
    public void onGeofenceTransition(TransitionGeofence geofence) {
        //showGeofence(geofence.getGeofenceModel().toGeofence(), geofence.getTransitionType());
    }

    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }

    private String getTransitionNameFromType(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            default:
                return "dwell";
        }
    }


}
