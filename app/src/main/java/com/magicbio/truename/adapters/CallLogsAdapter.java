package com.magicbio.truename.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Contacts;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.activeandroid.RecordModel;
import com.magicbio.truename.activities.CallDetails;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.utils.ContactUtils;

import java.util.List;


/**
 * Created by Bilal on 12/5/2017.
 */

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> {

    List<CallLogModel> CallLogModelList;
    Context context;
    String language, user;
    RecyclerView parent;
    int height, width;
    int prev = -1;

    public CallLogsAdapter(List<CallLogModel> CallLogModelList, Context context) {
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_log_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CallLogModel model = CallLogModelList.get(position);
        holder.btnView.setVisibility(View.GONE);
        holder.txtName.setText(model.getName());
        holder.txtNumber.setText(model.getPhNumber());
        holder.txtDuration.setText(model.getCallDuration());
        final Contact contact = Contact.getRandom(CallLogModelList.get(position).getPhNumber());
        if (contact != null) {
            Glide.with(context).load(contact.getImage()).into(holder.img);
        } else {
            Glide.with(context).load(R.drawable.picture_hot_call_section).into(holder.img);
        }
        if (model.getCallType().equals("OUTGOING")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.dialed_call1));
            if (model.getSim().equals("1")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_dialed_call));
            } else if (model.getSim().equals("0")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_dialed_call));
            }
        } else if (model.getCallType().equals("INCOMING")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.recieve_call1));
            if (model.getSim().equals("1")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_recieve_call));
            } else if (model.getSim().equals("0")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_recieve_call));
            }
        } else if (model.getCallType().equals("MISSED")) {
            holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.missed_call1));
            if (model.getSim().equals("1")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_missed_call));
            } else if (model.getSim().equals("0")) {
                holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_missed_call));
            }
        }
        holder.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CallDetails.class);
                intent.putExtra("name", model.getName());
                intent.putExtra("number", model.getPhNumber());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.CALL_PHONE}, 99);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.getPhNumber()));
                        context.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.getPhNumber()));
                    context.startActivity(intent);
                }
            }
        });

        holder.rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordModel r = RecordModel.getRandom(model.get_Id());
                if (r != null) {
//                    Uri path = Uri.parse(r.getPath());
//                    String newPath = path.toString();
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    File file = new File(newPath);
//                    intent.setDataAndType(Uri.fromFile(file), "*");
//                    context.startActivity(intent);
                    Toast.makeText(context, r.getPath(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "recording not avilable", Toast.LENGTH_LONG).show();
                }

            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + model.getPhNumber());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                context.startActivity(intent);
            }
        });

        holder.btnwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openWhatsAppChat(model.getPhNumber(), v.getContext());
            }
        });

        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnView.getVisibility() == View.VISIBLE) {
                    //slidefromLeftToRight(holder.btnView,holder.main_view,position);
                } else {
                    holder.btnView.setVisibility(View.VISIBLE);
                    slidefromRightToLeft(holder.btnView, holder.main_view, position);

                }
            }
        });

//        if(position==prev&&prev!=-1)
//        {
//            slidefromLeftToRight(holder.btnView,holder.main_view,position);
//        }


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

    public void slidefromRightToLeft(View view, View view1, final int pos) {

        TranslateAnimation animate;

        animate = new TranslateAnimation(view1.getWidth(), 0, 0, 0); // View for animation


        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // notifyDataSetChanged();
//                MyViewHolder holder= (MyViewHolder) parent.findViewHolderForAdapterPosition(prev);
//                slidefromLeftToRight(holder.btnView,holder.main_view,pos);
            }
        }, 500);
        // Change visibility VISIBLE or GONE
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        parent = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void slidefromLeftToRight(final View view, View view1, final int pos) {

        if (view.getVisibility() == View.VISIBLE) {
            TranslateAnimation animate;
            animate = new TranslateAnimation(0, view1.getWidth(), 0, 0); // View for animation
            animate.setDuration(500);
            animate.setFillAfter(true);
            view.startAnimation(animate);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    view.setVisibility(View.GONE);
                    prev = pos;
                }
            }, 500);
        }
        // Change visibility VISIBLE or GONE
    }

    private String getContactIdFromNumber(String number) {
        String[] projection = new String[]{Contacts.Phones._ID};
        Uri contactUri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL,
                Uri.encode(number));
        Cursor c = context.getContentResolver().query(contactUri, projection,
                null, null, null);
        if (c.moveToFirst()) {
            String contactId = c.getString(c.getColumnIndex(Contacts.Phones._ID));
            return contactId;
        }
        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber, txtDuration;
        RelativeLayout main_view;
        Button rec, btnHistory, btnwa, btnSms, btnCall;
        ImageView img;

        LinearLayout btnView;
        ImageView CallType, sim;

        public MyViewHolder(View itemView) {
            super(itemView);
//            row_linearlayout=(RelativeLayout)itemView.findViewById(R.id.notification_row_layout);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            CallType = itemView.findViewById(R.id.callType);
            sim = itemView.findViewById(R.id.sim);
            rec = itemView.findViewById(R.id.rec);
            btnView = itemView.findViewById(R.id.btnView);
            main_view = itemView.findViewById(R.id.main_view);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnwa = itemView.findViewById(R.id.btnwa);
            btnSms = itemView.findViewById(R.id.btnSms);
            btnCall = itemView.findViewById(R.id.btnCall);
            img = itemView.findViewById(R.id.img);


//            ViewGroup.LayoutParams lp=btnView.getLayoutParams();
//            lp.height=btnSms.getWidth();
//            btnView.setLayoutParams(lp);
        }
    }
}


