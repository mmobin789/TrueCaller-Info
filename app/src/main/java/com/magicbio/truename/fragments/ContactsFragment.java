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
import com.magicbio.truename.adapters.ContactsAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement
 * an interface to handle interaction events.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private TextView tvLoading;
    private boolean search, init;
    private int offset;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = view.findViewById(R.id.recycler_View);
        tvLoading = view.findViewById(R.id.tvLoading);
        return view;
    }

    public void showPermissionView() {
        if (getView() == null)
            return;

        tvLoading.setText(R.string.grant_numbers_permission);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        MainActivity mainActivity = (MainActivity) requireActivity();
        WorkManager.getInstance(mainActivity).getWorkInfosByTagLiveData("c").observe(getViewLifecycleOwner(), workInfo -> {
            if (!init && !workInfo.isEmpty() && workInfo.get(0).getProgress() == Data.EMPTY) {
                loadContacts();
            }

        });
    }

    private void init() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {

                int lastItemPosition = contactsAdapter.getItemCount() - 1;
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                boolean end = lastItemPosition == lastVisibleItem;

                if (dy > 0 && end && !search) {
                    loadContacts();
                }
            }
        });

        setContactsAdapter();


    }

    private void loadContacts() {
        tvLoading.setVisibility(View.VISIBLE);
        AppAsyncWorker.loadContacts(offset, (contacts) -> {
            if (!contacts.isEmpty()) {
                offset += 50;
                tvLoading.setVisibility(View.GONE);
                contactsAdapter.addContacts(contacts);
                init = true;
            }
            return null;
        });

    }


    public void search(@NotNull String newText) {
        if (getView() == null)
            return;

        search = !newText.isEmpty();
        contactsAdapter.setContacts(AppAsyncWorker.loadContactsBy(newText.trim()));
        offset = 0;
    }


    private void setContactsAdapter() {
        contactsAdapter = new ContactsAdapter((MainActivity) getActivity(), new ArrayList<>(500));
        recyclerView.setAdapter(contactsAdapter);

    }


    //  Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    // Log.i("Contacts_JSON", gson.toJson(contacts));


}
