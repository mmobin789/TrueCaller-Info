package com.magicbio.truename.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.utils.ContactUtils;

import java.util.ArrayList;


/**
 * Created by Bilal on 12/5/2017.
 */

public class CallDetailsAdapter extends RecyclerView.Adapter<CallDetailsAdapter.MyViewHolder> {

    private ArrayList<String> numbers;

    public CallDetailsAdapter(ArrayList<String> numbers) {
        this.numbers = numbers;
        //Toast.makeText(context,""+smsList.size(),Toast.LENGTH_LONG).show();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row_call_history, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.callNumber(numbers.get(holder.getAdapterPosition()));
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openSmsApp(numbers.get(holder.getAdapterPosition()));
            }
        });


        return holder;
    }


  /*  @Override
    public void search(@Nullable String s, @Nullable Function0<Unit> onNothingFound) {
        if (s != null && s.matches(Patterns.PHONE.pattern()))
            Contact.setSearchByNumber();
        else Contact.setSearchByName();


        super.search(s, onNothingFound);
    }*/

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtNumber.setText(numbers.get(position));
    }




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



    @Override
    public int getItemCount() {
        return numbers.size();
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
