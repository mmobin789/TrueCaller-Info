package com.magicbio.truename.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.magicbio.truename.R;
import com.magicbio.truename.adapters.SMSAdapter;
import com.magicbio.truename.models.Sms;
import com.magicbio.truename.utils.SimpleCountDownTimer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement an interface
 * to handle interaction events.
 */
public class MessagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SMSAdapter smsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Sms> smsList;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);

        init(v);

        return v;
    }


    private void init(View v) {
        recyclerView = v.findViewById(R.id.recycler_View);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        final SimpleCountDownTimer simpleCountDownTimer = new SimpleCountDownTimer(0, 1, new SimpleCountDownTimer.OnCountDownListener() {
            @Override
            public void onCountDownActive(@NotNull String time) {
                smsList = getAllSms();
            }

            @Override
            public void onCountDownFinished() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        smsAdapter = new SMSAdapter(smsList, recyclerView.getContext());
                        recyclerView.setAdapter(smsAdapter);
                    }
                });
            }
        }, 1);

        simpleCountDownTimer.runOnBackgroundThread();

        simpleCountDownTimer.start(false);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleCountDownTimer.start(false);
            }
        });


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

    private List<Sms> getAllSms() {
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





   /* public void search(String newText) {
        smsAdapter.search(newText, null);
    }*/


}
