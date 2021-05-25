package com.magicbio.truename.activities;

 import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;
import com.magicbio.truename.adapters.MainPagerAdapter;
import com.magicbio.truename.fragments.CallLogFragment;
import com.magicbio.truename.fragments.ContactsFragment;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.retrofit.ApiClient;
import com.magicbio.truename.retrofit.ApiInterface;
import com.magicbio.truename.utils.ContactUtils;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ImageView btnCalls, btnMasseges, btnContacts;
    private CallLogFragment callLogFragment = new CallLogFragment();
    //  private MessagesFragment messagesFragment = new MessagesFragment();
    private ContactsFragment contactsFragment = new ContactsFragment();
    private View l1, l2, l3;
    private SearchView searchView;
    private ViewPager viewPager;
    private AlertDialog alertDialog;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnCalls = findViewById(R.id.btnCalls);
        btnContacts = findViewById(R.id.btnContacts);
        btnMasseges = findViewById(R.id.btnMessages);
        viewPager = findViewById(R.id.vp);
        viewPager.setAdapter(new MainPagerAdapter(callLogFragment, contactsFragment, getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        LinearLayout v1 = findViewById(R.id.v1);
        LinearLayout v2 = findViewById(R.id.v2);
        LinearLayout v3 = findViewById(R.id.v3);
        final ImageView logoView = findViewById(R.id.logoView);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        TextView textView = searchView.findViewById(R.id.search_src_text);
        textView.setHintTextColor(Color.WHITE);
        textView.setTextColor(Color.WHITE);

        searchView.setOnCloseListener(() -> {
            logoView.setVisibility(View.VISIBLE);
            return false;
        });

        findViewById(R.id.rlSearch).setOnClickListener(view -> {
            searchView.setIconified(false);
        });

        searchView.setOnSearchClickListener(view -> logoView.setVisibility(View.GONE));

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

        v2.setOnClickListener(v -> {
            tab2();
            viewPager.setCurrentItem(1);
        });

        v1.setOnClickListener(v -> {
            tab1();
            viewPager.setCurrentItem(0);
        });

        v3.setOnClickListener(v -> {
            tab3();
            viewPager.setCurrentItem(2);
        });

        viewPager.setCurrentItem(1);

        //   Info info = TrueName.getUserInfo(getApplicationContext());
        //getCallDetails();

//      if(com.facebook.AccessToken.getCurrentAccessToken()==null)
//      {
//          startActivity(facebookActivity.getIntent());
//      }


      /*  new GraphRequest(
                com.facebook.AccessToken.getCurrentAccessToken(),
                "/263866514295586/",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("apiresponse", response.toString());
                    }
                }
        ).executeAsync();*/
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        AppAsyncWorker.saveContactsToDb(apiInterface, TrueName.getUserId(this));
        AppAsyncWorker.saveCallLogToDb();
        ContactUtils.sendSMSToPhoneBook(apiInterface, true);

        createExitDialog();

    }

    private void createExitDialog() {
        alertDialog = new AlertDialog.Builder(this).setMessage("Are you sure you want to exit ?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> MainActivity.super.onBackPressed()).setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel()).create();
    }

    @Override
    public void onBackPressed() {
        alertDialog.show();
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
        //      getSupportFragmentManager().beginTransaction().replace(R.id.fL, messagesFragment).commitNow();

    }

    private void tab2() {
        searchView.setVisibility(View.VISIBLE);
        btnCalls.setImageResource(R.drawable.call_btn_click1);
        btnMasseges.setImageResource(R.drawable.messege_btn_unclick1);
        btnContacts.setImageResource(R.drawable.contact_unclick1);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.VISIBLE);
        l3.setVisibility(View.GONE);
        //  FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fL, callLogFragment);
      /*  if (commitNow)
            fragmentTransaction.commitNow();
        else fragmentTransaction.commit();*/
    }

    private void tab3() {
        searchView.setVisibility(View.VISIBLE);
        btnCalls.setImageResource(R.drawable.call_btn_unclick1);
        btnMasseges.setImageResource(R.drawable.messege_btn_unclick1);
        btnContacts.setImageResource(R.drawable.contact_click1);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.VISIBLE);
        //   getSupportFragmentManager().beginTransaction().replace(R.id.fL, contactsFragment).commitNow();
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
