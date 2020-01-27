package com.magicbio.truename.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.activeandroid.ActiveAndroid;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.fragments.CallLogFragment;
import com.magicbio.truename.fragments.ContactsFragment;
import com.magicbio.truename.fragments.MessagesFragment;
import com.magicbio.truename.models.ContactModel;
import com.magicbio.truename.models.Info;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int REQUEST_SEND_SMS = 9;
    private static final int REQUEST_MICROPHONE = 10;
    Button btnCalls, btnMasseges, btnContacts;
    CallLogFragment callfragment = new CallLogFragment();
    MessagesFragment messagesFragment = new MessagesFragment();
    ContactsFragment contactsFragment = new ContactsFragment();
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction;
    RelativeLayout v1, v2, v3;
    View l1, l2, l3;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnCalls = findViewById(R.id.btnCalls);
        btnContacts = findViewById(R.id.btnContacts);
        btnMasseges = findViewById(R.id.btnMessages);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);
        v3 = findViewById(R.id.v3);
        final ImageView logoView = findViewById(R.id.logoView);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                logoView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoView.setVisibility(View.GONE);

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_MICROPHONE);

                }
            }
        }, 1000);


        btnCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
                transaction = manager.beginTransaction();
                transaction.replace(R.id.main_layout, callfragment, "call");
                transaction.commit();

                btnCalls.setBackground(getResources().getDrawable(R.drawable.call_btn_click1));
                btnMasseges.setBackground(getResources().getDrawable(R.drawable.messege_btn_unclick1));
                btnContacts.setBackground(getResources().getDrawable(R.drawable.contact_unclick1));
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.VISIBLE);
                l3.setVisibility(View.GONE);
            }
        });

        btnMasseges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(true);
                searchView.setVisibility(View.GONE);
                transaction = manager.beginTransaction();
                transaction.replace(R.id.main_layout, messagesFragment, "message");
                transaction.commit();

                btnCalls.setBackground(getResources().getDrawable(R.drawable.call_btn_unclick1));
                btnMasseges.setBackground(getResources().getDrawable(R.drawable.messege_btn_click1));
                btnContacts.setBackground(getResources().getDrawable(R.drawable.contact_unclick1));
                l1.setVisibility(View.VISIBLE);
                l2.setVisibility(View.GONE);
                l3.setVisibility(View.GONE);
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
                transaction = manager.beginTransaction();
                transaction.replace(R.id.main_layout, contactsFragment, "contacts");
                transaction.commit();

                btnCalls.setBackground(getResources().getDrawable(R.drawable.call_btn_unclick1));
                btnMasseges.setBackground(getResources().getDrawable(R.drawable.messege_btn_unclick1));
                btnContacts.setBackground(getResources().getDrawable(R.drawable.contact_click1));

                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);
                l3.setVisibility(View.VISIBLE);
            }
        });

        btnCalls.performClick();

        Info info = TrueName.getUserInfo(getApplicationContext());
        //getCallDetails();
        CheckPerm();
//      if(com.facebook.AccessToken.getCurrentAccessToken()==null)
//      {
//          startActivity(facebookActivity.getIntent());
//      }


        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        Log.d("token", accessToken.getToken());
        new GraphRequest(
                com.facebook.AccessToken.getCurrentAccessToken(),
                "/263866514295586/",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("apiresponse", response.toString());
                    }
                }
        ).executeAsync();
        // new LongOperation().execute("");


    }

    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number); // mobile number
            String callType = managedCursor.getString(type); // call type
            String callDate = managedCursor.getString(date); // call date
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
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
            }

        }
        managedCursor.close();
        //miss_cal.setText(sb);
        Log.e("Agil value --- ", sb.toString());
    }

    public void CheckPerm() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,}, REQUEST_SEND_SMS);
        } else {
            //TODO
        }
    }

    private void displayMessages() {
        ContentResolver cr = this.getContentResolver();


        try {
            Cursor cursor = cr.query(Uri.parse("content://mms-sms/conversations"), new String[]{"*"}, null, null, "date ASC"); //contID is the unique ID for one contact in our android.
            DatabaseUtils.dumpCursor(cursor);
            while (cursor.moveToNext()) {
                Log.d("Number: ", cursor.getString(cursor.getColumnIndexOrThrow("address")));
                Log.d("Body : ", cursor.getString(cursor.getColumnIndexOrThrow("body")));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ContactModel> getContactList() {
        List<ContactModel> contactModels = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        DatabaseUtils.dumpCursor(cur);
        ActiveAndroid.beginTransaction();

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContactModel contactModel = new ContactModel();
                Contact contact = new Contact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String image = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        contactModel.setName(name);
                        contactModel.setImage(image);
                        contactModel.setNumber(phoneNo);
                        try {
                            contact.setName(name);
                            contact.setNumber(phoneNo);
                            contact.setImage(image);
                            contact.save();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        contactModels.add(contactModel);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        return contactModels;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SEND_SMS:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    Toast.makeText(this, "Phone permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_MICROPHONE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }


    private void search(String newText) {
        if (callfragment.isVisible()) {
            callfragment.search(newText);
        } else if (contactsFragment.isVisible()) {
            contactsFragment.search(newText);
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            getContactList();


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

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
    }
}
