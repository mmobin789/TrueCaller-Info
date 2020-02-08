package com.magicbio.truename.adapters;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by Bilal on 12/5/2017.
 */

public class CallDetailsAdapter extends DynamicSearchAdapter<Contact> {

    private ArrayList<Contact> CallLogModelList;

    public CallDetailsAdapter(ArrayList<Contact> CallLogModelList) {
        super(CallLogModelList);
        this.CallLogModelList = CallLogModelList;
        //Toast.makeText(context,""+smsList.size(),Toast.LENGTH_LONG).show();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row_call_history, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.callNumber(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openSmsApp(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
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
        holder.txtNumber.setText(contact.getNumber());


//        Glide.with(context)
//                .load(smsList.get(position).getImage())
//                .centerCrop()
//                .into(holder.img);

        //holder.txtTimeAndN.setText(smsList.get(position).getNumber());

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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumber;
        ImageView btnCall, btnSms;

        MyViewHolder(View itemView) {
            super(itemView);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            // txtTimeAndN=itemView.findViewById(R.id.txtNumber);
        }
    }
}
