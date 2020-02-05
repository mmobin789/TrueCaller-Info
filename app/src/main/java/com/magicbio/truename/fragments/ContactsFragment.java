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
import com.magicbio.truename.activities.MainActivity;
import com.magicbio.truename.adapters.ContactsAdapter;
import com.magicbio.truename.utils.SimpleCountDownTimer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement
 * an interface to handle interaction events.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private boolean adShown;
    private List<Contact> contactList;

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

        SimpleCountDownTimer simpleCountDownTimer = new SimpleCountDownTimer(0, 1, new SimpleCountDownTimer.OnCountDownListener() {
            @Override
            public void onCountDownActive(@NotNull String time) {
                ((MainActivity) getActivity()).getContactList();
                contactList = Contact.getAll();
            }

            @Override
            public void onCountDownFinished() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter = new ContactsAdapter(contactList, getActivity());
                        recyclerView.setAdapter(contactsAdapter);
                        if (!adShown)
                            contactsAdapter.showAd();

                        adShown = true;
                    }
                });
            }
        }, 1);

        simpleCountDownTimer.runOnBackgroundThread();

        simpleCountDownTimer.start(false);


    }


    public void search(String newText) {
        contactsAdapter.search(newText.toLowerCase(), null);
    }



}
