package com.magicbio.truename.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;

import java.util.List;


/**
 * Created by Bilal on 12/5/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    List<Contact> CallLogModelList;
    Context context;
    String language, user;

    public ContactsAdapter(List<Contact> CallLogModelList, Context context) {
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        //Toast.makeText(context,""+CallLogModelList.size(),Toast.LENGTH_LONG).show();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.txtName.setText(CallLogModelList.get(position).getName());
        holder.txtNumber.setText(CallLogModelList.get(position).getNumber());
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", CallLogModelList.get(position).getNumber(), null));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + CallLogModelList.get(position).getNumber());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("sms_body", "");
                v.getContext().startActivity(intent);
            }
        });

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

    public void editContact(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        Button btnCall, btnSms;
        RelativeLayout row_linearlayout;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            row_linearlayout = itemView.findViewById(R.id.notification_row_layout);
            txtName = itemView.findViewById(R.id.txtName);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            img = itemView.findViewById(R.id.img);
            // txtTimeAndN=itemView.findViewById(R.id.txtNumber);
        }
    }
}
