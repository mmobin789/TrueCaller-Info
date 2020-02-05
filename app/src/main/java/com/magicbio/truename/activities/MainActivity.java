package com.magicbio.truename.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.activeandroid.ActiveAndroid;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.adapters.MainPagerAdapter;
import com.magicbio.truename.fragments.CallLogFragment;
import com.magicbio.truename.fragments.ContactsFragment;
import com.magicbio.truename.models.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private ImageView btnCalls, btnMasseges, btnContacts;
    private CallLogFragment callLogFragment = new CallLogFragment();
    private ContactsFragment contactsFragment = new ContactsFragment();
    private View l1, l2, l3;
    private ViewPager viewPager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnCalls = findViewById(R.id.btnCalls);
        btnContacts = findViewById(R.id.btnContacts);
        btnMasseges = findViewById(R.id.btnMessages);
        viewPager = findViewById(R.id.vp);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MainPagerAdapter(callLogFragment, contactsFragment, getSupportFragmentManager()));

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        LinearLayout v1 = findViewById(R.id.v1);
        LinearLayout v2 = findViewById(R.id.v2);
        LinearLayout v3 = findViewById(R.id.v3);
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0)
                    tab1();
                else if (position == 1)
                    tab2();
                else tab3();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab2();
                viewPager.setCurrentItem(1);
            }
        });

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab1();
                viewPager.setCurrentItem(0);
            }
        });

        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab3();
                viewPager.setCurrentItem(2);
            }
        });

        v1.performClick();

        //   Info info = TrueName.getUserInfo(getApplicationContext());
        //getCallDetails();

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

    private void tab1() {
        searchView.setIconified(true);
        searchView.setVisibility(View.GONE);
        btnCalls.setImageResource(R.drawable.call_btn_unclick1);
        btnMasseges.setImageResource(R.drawable.messege_btn_click1);
        btnContacts.setImageResource(R.drawable.contact_unclick1);
        l1.setVisibility(View.VISIBLE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);

    }

    private void tab2() {
        searchView.setVisibility(View.VISIBLE);
        btnCalls.setImageResource(R.drawable.call_btn_click1);
        btnMasseges.setImageResource(R.drawable.messege_btn_unclick1);
        btnContacts.setImageResource(R.drawable.contact_unclick1);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.VISIBLE);
        l3.setVisibility(View.GONE);
    }

    private void tab3() {
        searchView.setVisibility(View.VISIBLE);
        btnCalls.setImageResource(R.drawable.call_btn_unclick1);
        btnMasseges.setImageResource(R.drawable.messege_btn_unclick1);
        btnContacts.setImageResource(R.drawable.contact_click1);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.VISIBLE);
    }

/*    private void getCallDetails() {

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
    }*/

    public void getContactList() {
        List<ContactModel> contactModels = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        DatabaseUtils.dumpCursor(cur);
        ActiveAndroid.beginTransaction();

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
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
        // return contactModels;
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
        if (viewPager.getCurrentItem() == 1) {
            callLogFragment.search(newText);
        } else if (viewPager.getCurrentItem() == 2) {
            contactsFragment.search(newText);
        }
    }


}
