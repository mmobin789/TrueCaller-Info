package com.magicbio.truename.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.magicbio.truename.R;
import com.magicbio.truename.adapters.CallDetailsAdapter;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CallDetails extends AppCompatActivity {
    TextView txtName;
    ImageView btnMessage, btnCall, btnInvite, btnSave, btnLocation;
    RecyclerView recyclerView;
    String name;
    ArrayList<String> numbers;
    /*   private TextView locationText;
       private TextView activityText;
       private TextView geofenceText;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_details);
        txtName = findViewById(R.id.txtName);
        btnMessage = findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);
        btnInvite = findViewById(R.id.btnInvite);
        btnSave = findViewById(R.id.btnSave);
        btnLocation = findViewById(R.id.btnLocation);
        ImageView btnBlock = findViewById(R.id.btnBlock);
        TextView tvCountry = findViewById(R.id.tvCountry);
        TextView tvCountryBottom = findViewById(R.id.tvCountryBottom);
      /*  locationText = findViewById(R.id.sample);
        geofenceText = findViewById(R.id.sample);
        activityText = findViewById(R.id.sample);*/
        name = getIntent().getStringExtra("name");
        numbers = getIntent().getStringArrayListExtra("numbers");
        boolean byTrueName = getIntent().getBooleanExtra("by-true-name", false);
        String number = numbers.get(0);
        TextView tvWTAMessage = findViewById(R.id.tvMessageWhatsApp);
        TextView tvWTAAudio = findViewById(R.id.tvAudioWhatsApp);
        TextView tvWTAVideo = findViewById(R.id.tvVideoWhatsApp);
        TextView tvEmail = findViewById(R.id.tvEmail);
        tvWTAMessage.setText(String.format("Message %s", number));
        tvWTAAudio.setText(String.format("Voice %s", number));
        tvWTAVideo.setText(String.format("Video %s", number));
        AdUtils.loadBannerAd((AdView) findViewById(R.id.adView));
        String email = getIntent().getStringExtra("email");
        tvEmail.setText(email);

        boolean validName = ContactUtils.isContactName(name);

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmail.setVisibility(View.GONE);
        }

        if (validName && !byTrueName) {  // always true condition for contacts.
            btnSave.setVisibility(View.GONE);
            btnBlock.setVisibility(View.VISIBLE);
            btnBlock.setBackgroundResource(R.drawable.block_btn);
        } else {  // might be false for call logs.
            btnSave.setVisibility(View.VISIBLE);
            btnBlock.setVisibility(View.GONE);
            btnBlock.setBackgroundResource(R.drawable.unblock_btn);
            findViewById(R.id.llwhatsApp).setVisibility(View.GONE);
        }

        if (validName)
            txtName.setText(name);
        else txtName.setText(number);

        setupClick(number);

        CCPCountry ccpCountry = CCPCountry.getCountryForNumber(this, CountryCodePicker.Language.ENGLISH, number);

        if (ccpCountry != null) {
            tvCountry.setText(ccpCountry.getEnglishName());
            tvCountryBottom.setText(ccpCountry.getEnglishName());
        } else findViewById(R.id.llCountry).setVisibility(View.GONE);

        init();
    }


    public void openCallHistory(@NotNull View v) {
        startActivity(new Intent(v.getContext(), CallHistory.class).putStringArrayListExtra("numbers", numbers).putExtra("name", name));
    }

    public void makeWhatsAppAudioCall(View v) {
        ContactUtils.makeWhatsAppCall(name, false);
    }

    public void makeWhatsAppVideoCall(View v) {
        ContactUtils.makeWhatsAppCall(name, true);
    }

    private void setupClick(String number) {

        findViewById(R.id.llChat).setOnClickListener(v -> ContactUtils.openWhatsAppChat(number));

        btnMessage.setOnClickListener(v -> ContactUtils.openSmsApp(number));

        btnCall.setOnClickListener(v -> ContactUtils.openDialer(number));

        btnInvite.setOnClickListener(v -> {
            ContactUtils.sendInvite(number, v.getContext());

        });
        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
            startActivity(intent);
        });
        btnLocation.setOnClickListener(v -> ContactUtils.shareLocationOnSms(number, txtName.getText().toString()));

    }

    private void init() {
       /* ProgressDialog progressDialog = new ProgressDialog(CallDetails.this);
        progressDialog.setMessage("Please Wait.....");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);*/
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CallDetails.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //  recyclerView.setAdapter(new CallHistoryAdapter(getCallDetails(CallDetails.this, getIntent().getStringExtra("number"))));
        recyclerView.setAdapter(new CallDetailsAdapter(numbers));


    }




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
