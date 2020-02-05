package com.magicbio.truename.adapters;

import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.CommonAnimationUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by Bilal on 12/5/2017.
 */

public class ContactsAdapter extends DynamicSearchAdapter<Contact> {

    private List<Contact> CallLogModelList;
    private Context context;
    private int previousPosition = -1;

    public ContactsAdapter(List<Contact> CallLogModelList, Context context) {
        super(CallLogModelList);
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        //Toast.makeText(context,""+CallLogModelList.size(),Toast.LENGTH_LONG).show();
    }

    public void showAd() {
        int adPosition = AdUtils.getRandomAdPositionForList(3, getItemCount());
        CallLogModelList.get(adPosition).showAd = true;
        notifyItemChanged(adPosition);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openDialer(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openDialer(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Contact contact = CallLogModelList.get(position);
                if (previousPosition > -1) { // if previous opened close it
                    Contact contactOpened = CallLogModelList.get(previousPosition);
                    contactOpened.areOptionsShown = false;
                    notifyItemChanged(previousPosition);

                }
                // hidden so show
                CommonAnimationUtils.slideFromRightToLeft(holder.btnView, holder.rl.getWidth() - holder.img.getWidth());

                contact.areOptionsShown = true;

                previousPosition = position;
            }
        });
        return holder;
    }


    @Override
    public void search(@Nullable String s, @Nullable Function0<Unit> onNothingFound) {
        if (s != null && s.matches(Patterns.PHONE.pattern()))
            Contact.setSearchByNumber();
        else Contact.setSearchByName();

        super.search(s, onNothingFound);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        MyViewHolder holder = (MyViewHolder) vh;
        Contact contact = CallLogModelList.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtNumber.setText(contact.getNumber());

        if (contact.areOptionsShown)
            holder.btnCall.setVisibility(View.VISIBLE);
        else {
            holder.btnCall.setVisibility(View.GONE);
        }

        if (contact.showAd)
            holder.adView.setVisibility(View.VISIBLE);
        else holder.adView.setVisibility(View.GONE);

        Glide.with(context).load(CallLogModelList.get(position).getImage()).into(holder.img);
//        Glide.with(context)
//                .load(CallLogModelList.get(position).getImage())
//                .centerCrop()
//                .into(holder.img);

        //holder.txtTimeAndN.setText(CallLogModelList.get(position).getNumber());

//        if(position%2==0)
//        {
//            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
//        }
//        else
//        {
//            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
//        }

    }

    @Override
    public int getItemCount() {
        return CallLogModelList.size();
    }

  /*  public void editContact(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView btnCall, btnSms;
        ImageView img;
        AdView adView;
        RelativeLayout rl;
        LinearLayout btnView;

        MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
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
