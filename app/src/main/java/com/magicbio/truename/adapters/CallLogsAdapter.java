package com.magicbio.truename.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.activeandroid.RecordModel;
import com.magicbio.truename.activities.CallDetails;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static com.magicbio.truename.utils.CommonAnimationUtils.slideFromRightToLeft;


/**
 * Created by Bilal on 12/5/2017.
 */

public class CallLogsAdapter extends DynamicSearchAdapter<CallLogModel> {

    List<CallLogModel> CallLogModelList;
    Context context;
    String language, user;
    RecyclerView parent;
    int height, width;
    private int previousPosition = -1;

    public CallLogsAdapter(List<CallLogModel> CallLogModelList, Context context) {
        super(CallLogModelList);
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

    }

    public void showAd() {
        int adPosition = AdUtils.getRandomAdPositionForList(3, getItemCount());
        CallLogModelList.get(adPosition).showAd = true;
        notifyItemChanged(adPosition);
    }


    @Override
    public void search(@Nullable String s, @Nullable Function0<Unit> onNothingFound) {

        if (s != null && s.matches(Patterns.PHONE.pattern()))
            CallLogModel.setSearchByNumber();
        else CallLogModel.setSearchByName();

        super.search(s, onNothingFound);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_log_row, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        myViewHolder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();
                CallLogModel callLogModel = CallLogModelList.get(position);
                if (previousPosition > -1) { // if previous opened close it
                    CallLogModel callLogModelOpened = CallLogModelList.get(previousPosition);
                    callLogModelOpened.areOptionsShown = false;
                    notifyItemChanged(previousPosition);

                }
                // hidden so show
                slideFromRightToLeft(myViewHolder.btnView, myViewHolder.main_view.getWidth());

                callLogModel.areOptionsShown = true;

                previousPosition = position;


            }

        });

        myViewHolder.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
                Intent intent = new Intent(context, CallDetails.class);
                intent.putExtra("name", model.getName());
                intent.putExtra("number", model.getPhNumber());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        myViewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
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

        myViewHolder.rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
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
                    Toast.makeText(context, "recording not available", Toast.LENGTH_LONG).show();
                }

            }
        });
        myViewHolder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
                Uri uri = Uri.parse("smsto:" + model.getPhNumber());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                context.startActivity(intent);
            }
        });


        myViewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
                ContactUtils.shareLocationOnSms(model.getPhNumber(), model.getName());
            }
        });

        myViewHolder.btnwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
                ContactUtils.openWhatsAppChat(model.getPhNumber(), v.getContext());
            }
        });

        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        final CallLogModel model = CallLogModelList.get(position);

        holder.txtName.setText(model.getName());
        holder.txtNumber.setText(model.getPhNumber());
        holder.txtDuration.setText(model.getCallDuration());

        if (model.areOptionsShown)
            holder.btnView.setVisibility(View.VISIBLE);
        else {
            holder.btnView.setVisibility(View.GONE);
        }

        if (model.showAd)
            holder.adView.setVisibility(View.VISIBLE);
        else holder.adView.setVisibility(View.GONE);


        final Contact contact = Contact.getRandom(model.getPhNumber());
        if (contact != null)
            Glide.with(context).load(contact.getImage()).apply(RequestOptions.errorOf(R.drawable.logo1).placeholder(R.drawable.logo1)).into(holder.img);
        else
            Glide.with(context).load(R.drawable.logo1).into(holder.img);

        if (TextUtils.isEmpty(model.getName()))
            holder.btnLocation.setVisibility(View.GONE);
        else holder.btnLocation.setVisibility(View.VISIBLE);


        switch (model.getCallType()) {
            case "OUTGOING":
                holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.dialed_call1));
                if (model.getSim().equals("1")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_dialed_call));
                } else if (model.getSim().equals("0")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_dialed_call));
                }
                break;
            case "INCOMING":
                holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.recieve_call1));
                if (model.getSim().equals("1")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_recieve_call));
                } else if (model.getSim().equals("0")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_recieve_call));
                }
                break;
            case "MISSED":
                holder.CallType.setBackground(context.getResources().getDrawable(R.drawable.missed_call1));
                if (model.getSim().equals("1")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim2_missed_call));
                } else if (model.getSim().equals("0")) {
                    holder.sim.setBackground(context.getResources().getDrawable(R.drawable.sim1_missed_call));
                }
                break;
        }


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



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        parent = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber, txtDuration;
        RelativeLayout main_view;
        Button rec, btnHistory, btnwa, btnSms, btnCall, btnLocation;
        ImageView img;
        LinearLayout btnView;
        AdView adView;
        ImageView CallType, sim;

        MyViewHolder(View itemView) {
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
            btnLocation = itemView.findViewById(R.id.btnLocation);
            img = itemView.findViewById(R.id.img);
            adView = itemView.findViewById(R.id.adView);
            AdUtils.loadBannerAd(adView);


//            ViewGroup.LayoutParams lp=btnView.getLayoutParams();
//            lp.height=btnSms.getWidth();
//            btnView.setLayoutParams(lp);
        }
    }
}


