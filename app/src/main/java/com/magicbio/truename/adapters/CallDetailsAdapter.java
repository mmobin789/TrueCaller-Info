package com.magicbio.truename.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.models.CallLogModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Bilal on 12/5/2017.
 */

public class CallDetailsAdapter extends RecyclerView.Adapter<CallDetailsAdapter.MyViewHolder> {

    List<CallLogModel> CallLogModelList;
    Context context;
    String language, user;

    public CallDetailsAdapter(List<CallLogModel> CallLogModelList, Context context) {
        this.CallLogModelList = CallLogModelList;
        this.context = context;
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_details_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CallLogModel model = CallLogModelList.get(position);

        //holder.txtName.setText(model.getName());

        holder.txtNumber.setText(model.getPhNumber());
        holder.txtDuration.setText(model.getCallDuration());
        if (model.getCallType().equals("OUTGOING")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.dialled_call_icon));
        } else if (model.getCallType().equals("INCOMING")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.received_call_icon1));
        } else if (model.getCallType().equals("MISSED")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.miss_call));
        }
        if (model.getSim().equals("1")) {
            holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim_2));
        } else if (model.getSim().equals("0")) {
            holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim_1));
        }
        holder.txtDate.setText(getDate(Long.parseLong(model.getCallDate())));
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, CallDetails.class);
//                intent.putExtra("name",model.getName());
//                intent.putExtra("number",model.getPhNumber());
//                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                v.getContext().startActivity(intent);
//            }
//        });


        if (position % 2 == 0) {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
        } else {
            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumber, txtDuration, txtDate;
        RelativeLayout row_linearlayout;
        ImageView CallType, sim;

        public MyViewHolder(View itemView) {
            super(itemView);
            row_linearlayout = itemView.findViewById(R.id.notification_row_layout);
            //txtName=itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            CallType = itemView.findViewById(R.id.callType);
            sim = itemView.findViewById(R.id.sim);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
