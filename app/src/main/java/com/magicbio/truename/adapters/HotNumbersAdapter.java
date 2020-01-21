package com.magicbio.truename.adapters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magicbio.truename.R;
import com.magicbio.truename.models.CallLogModel;

import java.util.List;


/**
 * Created by Bilal on 12/5/2017.
 */

public class HotNumbersAdapter extends RecyclerView.Adapter<HotNumbersAdapter.MyViewHolder> {

    List<CallLogModel> CallLogModelList;
    Context context;
    String language, user;
    RecyclerView parent;
    int height, width;
    int prev = -1;

    public HotNumbersAdapter(List<CallLogModel> CallLogModelList, Context context) {
        this.CallLogModelList = CallLogModelList;
        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    public static boolean whatsappInstalledOrNot(String uri, Context c) {
        PackageManager pm = c.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void openWhatsApp(String number, Context c) {
        try {
            number = number.replace(" ", "").replace("+", "");

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            c.startActivity(sendIntent);

        } catch (Exception e) {
            Log.e("error", "ERROR_OPEN_MESSANGER" + e.toString());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hot_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CallLogModel model = CallLogModelList.get(position);
        holder.txtName.setText(model.getName());
        holder.txtNumber.setText(model.getPhNumber());

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber, txtDuration;
        RelativeLayout main_view;
        Button rec, btnHistory, btnwa, btnSms, btnCall;

        LinearLayout btnView;
        ImageView CallType, sim;

        public MyViewHolder(View itemView) {
            super(itemView);
//            row_linearlayout=(RelativeLayout)itemView.findViewById(R.id.notification_row_layout);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);


//            ViewGroup.LayoutParams lp=btnView.getLayoutParams();
//            lp.height=btnSms.getWidth();
//            btnView.setLayoutParams(lp);
        }
    }
}


