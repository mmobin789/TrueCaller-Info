package com.magicbio.truename.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.magicbio.truename.R;
import com.magicbio.truename.adapters.SMSAdapter;
import com.magicbio.truename.fragments.background.FetchAllSms;
import com.magicbio.truename.models.Sms;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement an interface
 * to handle interaction events.
 */
public class MessagesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);

        init(v);

        setSmsAdapter();

        return v;
    }


    private void setSmsAdapter() {
        FetchAllSms fetchAllSms = new FetchAllSms();
        fetchAllSms.setOnComplete(new Function1<ArrayList<Sms>, Unit>() {
            @Override
            public Unit invoke(ArrayList<Sms> sms) {
                recyclerView.setAdapter(new SMSAdapter(sms));
                return Unit.INSTANCE;
            }
        });


        fetchAllSms.execute(getContext());
    }


    private void init(View v) {
        recyclerView = v.findViewById(R.id.recycler_View);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSmsAdapter();
                swipeRefreshLayout.setRefreshing(false);
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







   /* public void search(String newText) {
        smsAdapter.search(newText, null);
    }*/


}
