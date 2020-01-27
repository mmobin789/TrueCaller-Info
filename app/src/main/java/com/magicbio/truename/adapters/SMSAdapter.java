package com.magicbio.truename.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Patterns;
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
import com.magicbio.truename.activities.SmsConversation;
import com.magicbio.truename.models.Sms;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by Bilal on 12/5/2017.
 */

public class SMSAdapter extends DynamicSearchAdapter<Sms> {

    List<Sms> CallLogModelList;
    Context context;
    String language, user;

    public SMSAdapter(List<Sms> CallLogModelList, Context context) {
        super(CallLogModelList);
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        //Toast.makeText(context,""+CallLogModelList.size(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void search(@Nullable String s, @Nullable Function0<Unit> onNothingFound) {
        if (s != null && s.matches(Patterns.PHONE.pattern()))
            Sms.setSearchByNumber();
        else Sms.setSearchByName();
        super.search(s, onNothingFound);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final Contact contact = Contact.getRandom(CallLogModelList.get(position).getAddress());
        Sms sms = CallLogModelList.get(position);
        //sms.setReadState(String.valueOf(getNumberOfUnreadMessages(context,sms.getId())));

        if (contact != null) {
            Glide.with(context).load(contact.getImage()).into(holder.img);
            if (Integer.valueOf(sms.getReadState()) >= 1) {
                holder.txtName.setText(contact.getName() + "(" + sms.getReadState() + ")");
            } else {
                holder.txtName.setText(contact.getName());
            }
        } else {
            Glide.with(context).load(R.drawable.image_contact).into(holder.img);
            if (Integer.valueOf(sms.getReadState()) >= 1) {
                holder.txtName.setText(CallLogModelList.get(position).getAddress() + "(" + sms.getReadState() + ")");
            } else {
                holder.txtName.setText(CallLogModelList.get(position).getAddress());
            }

        }
        holder.txtNumber.setText(CallLogModelList.get(position).getMsg());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallLogModelList.get(position).getReadState().equals("1")) {
                    CallLogModelList.get(position).setReadState("0");
                    //notifyDataSetChanged();
                    //ContentValues values = new ContentValues();
                    // values.put("read", true);
                    //context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "thread_id=?" ,new String[] {CallLogModelList.get(position).getId()});
                }
                Intent intent = new Intent(context, SmsConversation.class);
                if (contact != null) {
                    intent.putExtra("name", contact.getName());
                } else {
                    intent.putExtra("name", CallLogModelList.get(position).getAddress());

                }
                intent.putExtra("thread_id", CallLogModelList.get(position).getId());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri uri = Uri.parse("smsto:"+CallLogModelList.get(position).getNumber());
                //Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // intent.putExtra("sms_body", "");
                //v.getContext().startActivity(intent);
            }
        });
        //holder.txtTimeAndN.setText(CallLogModelList.get(position).getNumber());

        if (Integer.valueOf(sms.getReadState()) >= 1) {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
        } else {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return CallLogModelList.size();
    }

    public void editSms(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        //intentInsertEdit.setType(SmssContract.Smss.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }

    public int getNumberOfUnreadMessages(Context context, String three) {
        final Uri SMS_INBOX_URI = Uri.parse("content://sms/inbox");
        Cursor inboxCursor = context.getContentResolver().query(SMS_INBOX_URI, null, "read = 0 and thread_id =" + three, null, null);
        int unreadMessagesCount = inboxCursor.getCount();
        inboxCursor.close();
        return unreadMessagesCount;
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
