package com.magicbio.truename.adapters;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.magicbio.truename.R;
import com.magicbio.truename.activities.MainActivity;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;

import static com.magicbio.truename.utils.CommonAnimationUtils.slideFromRightToLeft;


public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> {

    private final List<CallLogModel> list;
    private int previousPosition = -1;
    private MainActivity mainActivity;
    /*private SimpleCountDownTimer simpleCountDownTimer = new SimpleCountDownTimer(0, 1, new SimpleCountDownTimer.OnCountDownListener() {
        @Override
        public void onCountDownActive(@NotNull String time) {

        }

        @Override
        public void onCountDownFinished() {
            setOptionsClosed();

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });


        }
    }, 1);*/

    public CallLogsAdapter(List<CallLogModel> list, MainActivity mainActivity) {
        this.list = list;
        this.mainActivity = mainActivity;
    }

    public void addCallLogs(List<CallLogModel> callLogModels) {
        list.addAll(callLogModels);
        showAds(callLogModels);
        notifyItemRangeInserted(getItemCount(), callLogModels.size());
    }

    public void setCallLogs(List<CallLogModel> callLogModels) {
        list.clear();
        list.addAll(callLogModels);
        showAds(callLogModels);
        notifyDataSetChanged();
    }

  /*  private void setOptionsClosed() {
        for (CallLogModel callLog : smsList) {
            callLog.areOptionsShown = false;
        }
    }*/

    private void showAds(@NotNull List<CallLogModel> callLogModels) {

        for (int i = 0; i < callLogModels.size(); i++) {
            callLogModels.get(i).showAd = i % 5 == 0;
        }

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_log_row_new, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(itemView);

        myViewHolder.rl.setOnClickListener(v -> handleMenu(myViewHolder, false));

        myViewHolder.btnClose.setOnClickListener(v -> handleMenu(myViewHolder, true));


        myViewHolder.btnHistory.setOnClickListener(v -> {
            final CallLogModel model = list.get(myViewHolder.getAdapterPosition());
            AppAsyncWorker.getCallLogByNumber(model.id, callLogModel -> {
                if (callLogModel != null)
                    ContactUtils.openCallDetailsActivity(callLogModel);
                else ContactUtils.openCallDetailsActivity(model.getPhNumber());
                return Unit.INSTANCE;
            });

        });

        myViewHolder.btnCall.setOnClickListener(v -> {
            CallLogModel model = list.get(myViewHolder.getAdapterPosition());
            ContactUtils.callNumber(mainActivity,model.getPhNumber());
        });

        //  myViewHolder.rec.setOnClickListener(v -> {
         /*   CallLogModel model = CallLogModelList.get(myViewHolder.getAdapterPosition());
            RecordModel r = RecordModel.getRandom(model.get_Id());
            if (r != null) {
//                    Uri path = Uri.parse(r.getPath());
//                    String newPath = path.toString();
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    File file = new File(newPath);
//                    intent.setDataAndType(Uri.fromFile(file), "*");
//                    context.startActivity(intent);
                Toast.makeText(v.getContext(), r.getPath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(v.getContext(), "recording not available", Toast.LENGTH_LONG).show();
            }*/

        //    });
        myViewHolder.btnSms.setOnClickListener(v -> {
            CallLogModel model = list.get(myViewHolder.getAdapterPosition());
            Uri uri = Uri.parse("smsto:" + model.getPhNumber());
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "");
            v.getContext().startActivity(intent);
        });


        myViewHolder.btnLocation.setOnClickListener(v -> {
            CallLogModel model = list.get(myViewHolder.getAdapterPosition());
            ContactUtils.shareLocationOnSms(mainActivity,model.getPhNumber(), model.getName());
        });

        myViewHolder.btnwa.setOnClickListener(v -> {
            CallLogModel model = list.get(myViewHolder.getAdapterPosition());
            ContactUtils.openWhatsAppChat(model.getPhNumber());
        });

        return myViewHolder;
    }

    private void handleMenu(@NotNull MyViewHolder myViewHolder, boolean closeOnly) {
        int position = myViewHolder.getAdapterPosition();

        CallLogModel callLogModel = list.get(position);

        if (closeOnly) {
            callLogModel.areOptionsShown = false;
            notifyItemChanged(position);
            return;
        }

        if (previousPosition > -1) { // if previous opened close it
            CallLogModel callLogModelOpened = list.get(previousPosition);
            callLogModelOpened.areOptionsShown = false;
            notifyItemChanged(previousPosition);

        }
        // hidden so show
        slideFromRightToLeft(myViewHolder.btnView, myViewHolder.rl.getWidth() - myViewHolder.img.getWidth());

        callLogModel.areOptionsShown = true;

        previousPosition = position;
    }

    /*@Nullable
    private String formatTime(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd H:mm:s z yyyy", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = simpleDateFormat.parse(time);
            simpleDateFormat.applyPattern("dd/MM/yyyy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return simpleDateFormat.format(date).toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    @Nullable
    private String formatDuration(String duration) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:s", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(duration);
            Calendar calendar = Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);
            int hours = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);
            String withHours = hours + "h " + minutes + "m " + seconds + "s";
            String withMinutes = minutes + "m " + seconds + "s";
            if (hours > 0)
                return withHours;
            return withMinutes;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final CallLogModel model = list.get(position);
        String name = model.getName();
        String number = model.getPhNumber();

        if (!ContactUtils.isContactName(name)) {
            holder.txtName.setText(number);
            AppAsyncWorker.addCallLog(holder, model, (callLogModel, mvh) -> {
                mvh.txtName.setText(callLogModel.getName());
                mvh.logo.setVisibility(View.VISIBLE);
                return null;
            });
        } else {
            holder.txtName.setText(name);
        }
        holder.txtNumber.setText(number);
        String time = formatDuration(model.getCallDuration()) + "\t" + model.getCallDayTime();
        holder.txtDuration.setText(time);

        if (model.areOptionsShown)
            holder.btnView.setVisibility(View.VISIBLE);
        else {
            holder.btnView.setVisibility(View.GONE);
        }

        if (model.showAd)
            holder.adView.setVisibility(View.VISIBLE);
        else holder.adView.setVisibility(View.GONE);


   /*     if (TextUtils.isEmpty(model.getName()))
            holder.btnLocation.setVisibility(View.GONE);
        else holder.btnLocation.setVisibility(View.VISIBLE);*/


        if (model.numberByTrueName) {
            holder.logo.setVisibility(View.VISIBLE);
        } else holder.logo.setVisibility(View.GONE);


        switch (model.getCallType()) {
            case "OUTGOING":
                holder.CallType.setImageResource(R.drawable.dialled_call);
              //  holder.sim.setImageResource(R.drawable.sim1_dialed_call);
                if (model.getSim().equals("0")) {
                    holder.sim.setImageResource(R.drawable.sim1_dialed_call);
                } else if (model.getSim().equals("1")) {
                    holder.sim.setImageResource(R.drawable.sim2_dialed_call);
                }
                break;
            case "INCOMING":
                holder.CallType.setImageResource(R.drawable.recieve_call);
              //  holder.sim.setImageResource(R.drawable.sim1_dialed_call);
                if (model.getSim().equals("0")) {
                    holder.sim.setImageResource(R.drawable.sim1_dialed_call);
                } else if (model.getSim().equals("1")) {
                    holder.sim.setImageResource(R.drawable.sim2_dialed_call);
                }
                break;
            case "MISSED":
                holder.CallType.setImageResource(R.drawable.missed_call);
             //   holder.sim.setImageResource(R.drawable.sim1_missed_call);
                if (model.getSim().equals("0")) {
                    holder.sim.setImageResource(R.drawable.sim1_missed_call);
                } else if (model.getSim().equals("1")) {
                    holder.sim.setImageResource(R.drawable.sim2_missed_call);
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
        return list.size();
    }


    /*private String getContactIdFromNumber(String number) {
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
    }*/

    static public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber, txtDuration;
        RelativeLayout rl;
        Button btnHistory, btnwa, btnSms, btnLocation, btnClose;
        ImageView img, btnCall;
        LinearLayout btnView;
        AdView adView;
        ImageView CallType, sim, logo;

        MyViewHolder(View itemView) {
            super(itemView);
//            row_linearlayout=(RelativeLayout)itemView.findViewById(R.id.notification_row_layout);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            CallType = itemView.findViewById(R.id.callType);
            sim = itemView.findViewById(R.id.sim);
            btnClose = itemView.findViewById(R.id.btnBack);
            btnView = itemView.findViewById(R.id.btnView);
            rl = itemView.findViewById(R.id.rl);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnwa = itemView.findViewById(R.id.btnwa);
            btnSms = itemView.findViewById(R.id.btnSms);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnLocation = itemView.findViewById(R.id.btnLocation);
            img = itemView.findViewById(R.id.img);
            adView = itemView.findViewById(R.id.adView);
            logo = itemView.findViewById(R.id.ivLogo);
            AdUtils.loadBannerAd(adView);


//            ViewGroup.LayoutParams lp=btnView.getLayoutParams();
//            lp.height=btnSms.getWidth();
//            btnView.setLayoutParams(lp);
        }
    }
}


