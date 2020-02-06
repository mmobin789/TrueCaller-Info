package com.magicbio.truename.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.models.Sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Bilal on 12/5/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    public final static int TYPE_SENT = 0;
    public final static int TYPE_RECEIVED = 1;
    public String preDate = "";
    List<Sms> CallLogModelList;
    Context context;
    int height;
    int width;
    String language, user;

    public ConversationAdapter(List<Sms> CallLogModelList, Context context) {
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        //Toast.makeText(context,""+smsList.size(),Toast.LENGTH_LONG).show();
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_RECEIVED) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_sms_view, parent, false);
            // ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            //lp.width=width/2;
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_sms_view, parent, false);
            // ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            //lp.width=width/2;
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Sms sms = CallLogModelList.get(position);
        holder.txtSms.setText(sms.getMsg());
        String[] datetime = getDate(Long.parseLong(sms.getTime()), "dd/MM/yyyy hh:mm:ss").split(" ");
        if (!preDate.equals(datetime[0])) {
            holder.txtDate.setText(datetime[0]);
            holder.txtDate.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams lp = holder.txtDate.getLayoutParams();
            lp.height = 100;
        }
        preDate = datetime[0];
        holder.txtTime.setText(datetime[1]);
        if (sms.getSim().equals("1")) {
            holder.imgSim.setBackground(context.getResources().getDrawable(R.drawable.sim_2));
        } else if (sms.getSim().equals("0")) {
            holder.imgSim.setBackground(context.getResources().getDrawable(R.drawable.sim_1));
        }

        //holder.txtTimeAndN.setText(smsList.get(position).getNumber());
//
//        if(smsList.get(position).getReadState().equals("1"))
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

    public void editSms(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        //intentInsertEdit.setType(SmssContract.Smss.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }

    @Override
    public int getItemViewType(int position) {
        if (CallLogModelList.get(position).getFolderName().equals("inbox")) {
            return TYPE_RECEIVED;
        } else {
            return TYPE_SENT;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtSms, txtTime, txtDate;
        ImageView imgSim;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtSms = itemView.findViewById(R.id.txtSms);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgSim = itemView.findViewById(R.id.imgSim);
            // txtTimeAndN=itemView.findViewById(R.id.txtNumber);
        }
    }
}
