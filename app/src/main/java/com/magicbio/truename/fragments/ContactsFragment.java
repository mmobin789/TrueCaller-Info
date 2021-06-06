package com.magicbio.truename.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
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
    private int startId = 1;
    private int endId = 50;
    private boolean search;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        init(view);
        return view;
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
                if (dy > 0 && linearLayoutManager.findLastVisibleItemPosition() == contactsAdapter.getItemCount() - 1 && !search) {
                    loadContacts();
                }
            }
        });
        setContactsAdapter();


    }

    private void loadContacts() {
        AppAsyncWorker.loadContacts(startId, endId, contacts -> {
            tvLoading.setVisibility(View.GONE);
            contactsAdapter.addContacts(contacts);
            startId = endId + 1;
            endId += endId;
            return null;
        });
    }

    public void search(String newText) {
        AppAsyncWorker.loadContactsByName(newText, (contacts,search) -> {
            contactsAdapter.setContacts(contacts);
            startId = 1;
            endId = 50;
            this.search = search;
            return null;
        });
    }


    private void setContactsAdapter() {
        contactsAdapter = new ContactsAdapter(new ArrayList<>(500));
        recyclerView.setAdapter(contactsAdapter);
        loadContacts();

    }


        //  Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        // Log.i("Contacts_JSON", gson.toJson(contacts));





}
