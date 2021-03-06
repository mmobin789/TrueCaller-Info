package com.magicbio.truename.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.magicbio.truename.R;
import com.magicbio.truename.activities.MainActivity;
import com.magicbio.truename.adapters.CallLogsAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;

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
    private LinearLayoutManager layoutManager;
    private ImageView ivLoading;
    private boolean search, init;
    private int offset;

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
        ivLoading = v.findViewById(R.id.ivLoading);
        Glide.with(this).load(R.raw.loading).into(ivLoading);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        // reverseLayout();
        // layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {

                int lastItemPosition = callLogsAdapter.getItemCount() - 1;
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                boolean end = lastItemPosition == lastVisibleItem;

                if (dy > 0 && end && !search) {
                    loadCallLog();
                }
            }
        });
        setCallLogsAdapter();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        WorkManager.getInstance(mainActivity).getWorkInfosByTagLiveData("cl").observe(getViewLifecycleOwner(), workInfo -> {
            if (!init && !workInfo.isEmpty() && workInfo.get(0).getProgress() == Data.EMPTY) {
                loadCallLog();
            }
        });
    }

    public void showPermissionView() {
        if (getView() == null)
            return;

        Toast.makeText(getContext(), R.string.grant_numbers_permission, Toast.LENGTH_LONG).show();
    }


    private void loadCallLog() {
        ivLoading.setVisibility(View.VISIBLE);
        AppAsyncWorker.loadCallLog(offset, (callLog) -> {
            if (!callLog.isEmpty()) {
                offset += 50;
                ivLoading.setVisibility(View.GONE);
                callLogsAdapter.addCallLogs(callLog);
                init = true;
            }
            return null;
        });
    }


    private void setCallLogsAdapter() {
        callLogsAdapter = new CallLogsAdapter(new ArrayList<>(500), (MainActivity) getActivity());
        recyclerView.setAdapter(callLogsAdapter);
    }

    public void search(String newText) {
        if (getView() == null)
            return;
        AppAsyncWorker.loadCallLogsBy(newText.trim(), (callLog, search) -> {
            callLogsAdapter.setCallLogs(callLog);
            this.search = search;
            offset = 0;
            return null;
        });

    }


}
