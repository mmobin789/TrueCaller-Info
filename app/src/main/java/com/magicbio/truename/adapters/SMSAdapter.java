package com.magicbio.truename.adapters;

import android.content.Intent;
import android.graphics.Color;
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

    private List<Sms> smsList;

    public SMSAdapter(List<Sms> smsList) {
        super(smsList);
        this.smsList = smsList;
        //Toast.makeText(context,""+smsList.size(),Toast.LENGTH_LONG).show();
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
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();
                final Contact contact = Contact.getRandom(smsList.get(position).getAddress());
                if (smsList.get(position).getReadState().equals("1")) {
                    smsList.get(position).setReadState("0");
                    //notifyDataSetChanged();
                    //ContentValues values = new ContentValues();
                    // values.put("read", true);
                    //context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "thread_id=?" ,new String[] {smsList.get(position).getId()});
                }
                Intent intent = new Intent(v.getContext(), SmsConversation.class);

                if (contact != null) {
                    intent.putExtra("name", contact.getName());
                } else {
                    intent.putExtra("name", smsList.get(position).getAddress());

                }
                intent.putExtra("thread_id", smsList.get(position).getId());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final Contact contact = Contact.getRandom(smsList.get(position).getAddress());
        Sms sms = smsList.get(position);
        //sms.setReadState(String.valueOf(getNumberOfUnreadMessages(context,sms.getId())));

        if (contact != null) {
            Glide.with(viewHolder.itemView).load(contact.getImage()).into(holder.img);
            if (Integer.valueOf(sms.getReadState()) >= 1) {
                holder.txtName.setText(contact.getName() + "(" + sms.getReadState() + ")");
            } else {
                holder.txtName.setText(contact.getName());
            }
        } else {
            Glide.with(viewHolder.itemView).load(R.drawable.image_contact).into(holder.img);
            if (Integer.valueOf(sms.getReadState()) >= 1) {
                holder.txtName.setText(smsList.get(position).getAddress() + "(" + sms.getReadState() + ")");
            } else {
                holder.txtName.setText(smsList.get(position).getAddress());
            }

        }
        holder.txtNumber.setText(smsList.get(position).getMsg());


        //holder.txtTimeAndN.setText(smsList.get(position).getNumber());

        if (Integer.valueOf(sms.getReadState()) >= 1) {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
        } else {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

  /*  public void editSms(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        //intentInsertEdit.setType(SmssContract.Smss.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }*/

   /* public int getNumberOfUnreadMessages(Context context, String three) {
        final Uri SMS_INBOX_URI = Uri.parse("content://sms/inbox");
        Cursor inboxCursor = context.getContentResolver().query(SMS_INBOX_URI, null, "read = 0 and thread_id =" + three, null, null);
        int unreadMessagesCount = inboxCursor.getCount();
        inboxCursor.close();
        return unreadMessagesCount;
    }*/

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        Button btnCall, btnSms;
        RelativeLayout row_linearlayout;
        ImageView img;

        MyViewHolder(View itemView) {
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
