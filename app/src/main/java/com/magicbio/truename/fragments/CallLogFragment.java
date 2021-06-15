package com.magicbio.truename.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.WorkManager;

import com.magicbio.truename.R;
import com.magicbio.truename.activities.MainActivity;
import com.magicbio.truename.adapters.CallLogsAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.models.CallLogModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement an interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CallLogFragment extends Fragment {
    private RecyclerView recyclerView;
    private CallLogsAdapter callLogsAdapter;
    private TextView tvLoading;
    private int startId = 1;
    private int endId = 50;
    private boolean search;

    public CallLogFragment() {
        // Required empty public constructor
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
        recyclerView = v.findViewById(R.id.recycler_View);
        tvLoading = v.findViewById(R.id.tvLoading);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && linearLayoutManager.findLastVisibleItemPosition() == callLogsAdapter.getItemCount() - 1 && !search) {
                    loadCallLog();
                }
            }
        });
        setCallLogsAdapter();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        WorkManager.getInstance(mainActivity).getWorkInfosByTagLiveData("cl").observe(this, workInfo -> {
            if (workInfo.get(0).getProgress() == Data.EMPTY)
                loadCallLog();
        });
    }

    private void setIds() {
        CallLogModel callLogModel = AppAsyncWorker.get1stCallLog();
        if (callLogModel != null) {
            startId = callLogModel.id;
            endId = startId + 49;
        }
    }

    private void loadCallLog() {
        setIds();
        AppAsyncWorker.loadCallLog(startId, endId, (callLog) -> {
            if (!callLog.isEmpty()) {
                tvLoading.setVisibility(View.GONE);
                callLogsAdapter.addCallLogs(callLog);
                startId = endId + 1;
                endId += endId;
            }
            return null;
        });
    }


    private void setCallLogsAdapter() {
        callLogsAdapter = new CallLogsAdapter(new ArrayList<>(500));
        recyclerView.setAdapter(callLogsAdapter);
    }

    public void search(String newText) {
        requireActivity().runOnUiThread(() -> AppAsyncWorker.loadCallLogsByName(newText, (callLog, search) -> {
            callLogsAdapter.setCallLogs(callLog);
            this.search = search;
            setIds();
            return null;
        }));

    }


}
