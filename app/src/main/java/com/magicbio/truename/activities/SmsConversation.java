package com.magicbio.truename.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.adapters.ConversationAdapter;
import com.magicbio.truename.models.Sms;

import java.util.ArrayList;
import java.util.List;

public class SmsConversation extends AppCompatActivity {
    TextView txtName;
    Button btnback, btnSend;
    EditText textInput;
    RecyclerView recyclerView;
    List<Sms> smsList = new ArrayList<>();
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_conversation);
        btnback = findViewById(R.id.btnback);
        btnSend = findViewById(R.id.btnSend);
        textInput = findViewById(R.id.textInput);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocationSMS(smsList.get(0).getAddress(), textInput.getText().toString());
            }
        });
    }

    public void init() {
        txtName = findViewById(R.id.txtName);
        txtName.setText(getIntent().getStringExtra("name"));
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Please Wait.....");
        progressDoalog.setCancelable(false);
        progressDoalog.setIndeterminate(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        smsList = getAllSms(getIntent().getStringExtra("thread_id"));
        recyclerView.setAdapter(new ConversationAdapter(smsList, SmsConversation.this));
        recyclerView.scrollToPosition(smsList.size() - 1);
        //new LongOperation().execute(getIntent().getStringExtra("thread_id"));

    }

    public List<Sms> getAllSms(String threadid) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        // Uri message = Uri.parse("content://sms/conversations");
        ContentResolver cr = getContentResolver();
        //Telephony.Sms.PERSON;
        String[] strNumber = {threadid};
        //String[] projection = {"thread_id = ?", "MAX(date)", "COUNT(*) AS msg_count", "body","address"};

        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, "thread_id = ?", strNumber, "DATE asc");
        DatabaseUtils.dumpCursor(c);
        int totalSMS = c.getCount();
        try {


            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new Sms();
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setReadState(c.getString(c.getColumnIndex("read")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    objSms.setSim(c.getString(c.getColumnIndexOrThrow("sub_id")));

                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
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

    public void smsSendMessage(View view) {
        // Set the destination phone number to the string in editText.
        String destinationAddress = smsList.get(0).getAddress();
        // Find the sms_message view.

        // Get the text of the SMS message.
        String smsMessage = textInput.getText().toString();
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage
                (destinationAddress, scAddress, smsMessage,
                        sentIntent, deliveryIntent);
    }

    public void sendLocationSMS(String phoneNumber, String sms) {
        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        //String uri = "http://maps.google.com/maps?q=" + currentLocation.getLatitude()+","+currentLocation.getLongitude();
        //String uri1 = "<a href="+"http://maps.google.com/maps?q=" + currentLocation.getLatitude()+","+currentLocation.getLongitude()+">Link</a>";
        smsBody.append(Uri.parse(sms));
        //smsBody.append(currentLocation.getLatitude());
        //smsBody.append(",");
        //smsBody.append(currentLocation.getLongitude());
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);

        smsList = getAllSms(getIntent().getStringExtra("thread_id"));
        recyclerView.setAdapter(new ConversationAdapter(smsList, SmsConversation.this));


    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            progressDoalog.show();
            smsList = getAllSms(params.toString());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            progressDoalog.dismiss();
            recyclerView.setAdapter(new ConversationAdapter(smsList, getApplicationContext()));
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
