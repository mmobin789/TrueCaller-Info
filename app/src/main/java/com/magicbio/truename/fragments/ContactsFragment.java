package com.magicbio.truename.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.adapters.ContactsAdapter;
import com.magicbio.truename.fragments.background.FetchContacts;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement
 * an interface to handle interaction events.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private boolean adShown;

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
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        setContactsAdapter();


    }

    private void setContactsAdapter() {


        FetchContacts fetchContacts = new FetchContacts();
        fetchContacts.setOnComplete(new Function1<List<Contact>, Unit>() {
            @Override
            public Unit invoke(List<Contact> contacts) {
                setAdapter(contacts);
                return Unit.INSTANCE;
            }
        });

        fetchContacts.execute(getContext());
    }

    private void setAdapter(List<Contact> contacts) {
        contactsAdapter = new ContactsAdapter(contacts);
        recyclerView.setAdapter(contactsAdapter);
        if (!adShown) {
            contactsAdapter.showAd();
            adShown = true;
        }
    }


    public void search(String newText) {
        contactsAdapter.search(newText.toLowerCase(), null);
    }


}
