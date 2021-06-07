package com.magicbio.truename.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.magicbio.truename.R;
import com.magicbio.truename.db.contacts.Contact;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.CommonAnimationUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.pixel.Pixel;
import io.pixel.config.PixelOptions;


/**
 * Created by Bilal on 12/5/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private final ArrayList<Contact> contacts;
    private int previousPosition = -1;

    public ContactsAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContacts(List<Contact> contacts) {
        this.contacts.addAll(contacts);
        showAds(contacts);
        notifyItemRangeInserted(getItemCount(), contacts.size());
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts.clear();
        this.contacts.addAll(contacts);
        showAds(contacts);
        notifyDataSetChanged();
    }

    private void showAds(@NotNull List<Contact> contacts) {

        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).setShowAd(i % 5 == 0);
        }

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.btnCall.setOnClickListener(v -> ContactUtils.callNumber(contacts.get(holder.getAdapterPosition()).getNumbers().get(0)));

        holder.btnSms.setOnClickListener(v -> ContactUtils.openSmsApp(contacts.get(holder.getAdapterPosition()).getNumbers().get(0)));

        holder.btnClose.setOnClickListener(v -> handleMenu(holder, true));

        holder.btnLocation.setOnClickListener(v -> {
            Contact model = contacts.get(holder.getAdapterPosition());
            ContactUtils.shareLocationOnSms(model.getNumbers().get(0), model.getName());
        });

        holder.btnwa.setOnClickListener(v -> {
            Contact model = contacts.get(holder.getAdapterPosition());
            ContactUtils.openWhatsAppChat(model.getNumbers().get(0));
        });
        holder.btnHistory.setOnClickListener(v -> {
            Contact model = contacts.get(holder.getAdapterPosition());
            ContactUtils.openCallDetailsActivity(model);
        });

        holder.rl.setOnClickListener(view -> handleMenu(holder, false));

        return holder;
    }

    private void handleMenu(@NotNull MyViewHolder holder, boolean closeOnly) {
        int position = holder.getAdapterPosition();

        Contact contact = contacts.get(position);

        if (closeOnly) {
            contact.setAreOptionsShown(false);
            notifyItemChanged(position);
            return;
        }

        if (previousPosition > -1) { // if previous opened close it
            Contact contactOpened = contacts.get(previousPosition);
            contactOpened.setAreOptionsShown(false);
            notifyItemChanged(previousPosition);

        }
        // hidden so show
        CommonAnimationUtils.slideFromRightToLeft(holder.btnView, holder.rl.getWidth() - holder.img.getWidth());

        contact.setAreOptionsShown(true);

        previousPosition = position;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtNumber.setText(contact.getNumbers().get(0));

        if (contact.getAreOptionsShown())
            holder.btnView.setVisibility(View.VISIBLE);
        else {
            holder.btnView.setVisibility(View.GONE);
        }

        if (contact.getShowAd())
            holder.adView.setVisibility(View.VISIBLE);
        else holder.adView.setVisibility(View.GONE);

        Pixel.load(contact.getImage(), new PixelOptions.Builder().setPlaceholderResource(R.drawable.no_image).build(), holder.img);
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

  /*  public void editContact(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }*/

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView btnCall;
        ImageView img;
        AdView adView;
        RelativeLayout rl;
        LinearLayout btnView;
        Button btnSms, btnLocation, btnHistory, btnwa, btnClose;

        MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
            btnLocation = itemView.findViewById(R.id.btnLocation);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnwa = itemView.findViewById(R.id.btnwa);
            btnClose = itemView.findViewById(R.id.btnBack);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            img = itemView.findViewById(R.id.img);
            adView = itemView.findViewById(R.id.adView);
            rl = itemView.findViewById(R.id.rl);
            btnView = itemView.findViewById(R.id.btnView);
            AdUtils.loadBannerAd(adView);
            // txtTimeAndN=itemView.findViewById(R.id.txtNumber);
        }
    }
}
