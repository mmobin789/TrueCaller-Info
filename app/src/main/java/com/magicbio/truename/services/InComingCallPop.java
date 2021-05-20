package com.magicbio.truename.services;

/**
 * Created by Bilal on 8/16/2018.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.magicbio.truename.R;
import com.magicbio.truename.activities.ComFunc;
import com.magicbio.truename.models.CallLogModel;
import com.magicbio.truename.models.GetNumberResponse;
import com.magicbio.truename.models.Record;
import com.magicbio.truename.retrofit.ApiClient;
import com.magicbio.truename.retrofit.ApiInterface;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.pixel.Pixel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InComingCallPop extends Service {
    String number;
    TextView txtNumber, txtName, txtAddress, txtNetwork;
    ApiInterface apiInterface;
    MediaRecorder recorder;
    ImageView btnMessage, btnCall, btnInvite, btnSave;
    int ptype = -1;
    private int notificationId = 33;
    private NotificationManager nm;
    int MAX_CLICK_DURATION = 200;
    long startClickTime;
    AdView mAdView;
    InterstitialAd interstitialAd;
    String filepath;
    // variables
    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mPaperParams;

    private int windowWidth;
    // UI
    private View ivCrumpledPaper;
    private ImageView ivRecycleBin;
    private float lastActionDownX, lastActionDownY;

    // get intent methods
    public static Intent getIntent(Context context) {

        return new Intent(context, InComingCallPop.class);
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel-786";
            prepareChannel(this, channelId);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        return builder;
    }

    @TargetApi(26)
    private void prepareChannel(Context context, String id) {
        final String appName = context.getString(R.string.app_name);
        String description = context.getString(R.string.notifications_channel_description);


        if (nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, NotificationManager.IMPORTANCE_DEFAULT);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }


 /*   public static String getLastCall(CallLogModel callLogModel, Context context) {
        long differenceDates = 0;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String date = callLogModel.getCallDayTime();
        Date date1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            date1 = format.parse(date);
            System.out.println(date);
            long difference = Math.abs(date1.getTime() - new Date().getTime());
            differenceDates = difference;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Last Call " + differenceDates / 1000 + " Seconds ago";
    }*/

//        private void addRecycleBinView() {
//            // add recycle bin ImageView centered on the bottom of the screen
//            mRecycleBinParams = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//
//            mRecycleBinParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
//
//            ivRecycleBin = new ImageView(this);
//            ivRecycleBin.setImageResource(R.mipmap.bubble_trash_background);
//
//            mRecycleBinParams.x = 0;
//            mRecycleBinParams.y = 0;
//
//            mWindowManager.addView(ivRecycleBin, mRecycleBinParams);
//        }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    // methods
    @Override
    public void onCreate() {
        super.onCreate();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        nm = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
        createAndShowForegroundNotification(false);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            number = intent.getStringExtra("number");
            ptype = intent.getIntExtra("ptype", 0);
        }
        showHud();


        return START_STICKY;
    }

    private void createAndShowForegroundNotification(boolean cancel) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (cancel && nm != null) {
                nm.cancel(notificationId);
                return;
            }

            final NotificationCompat.Builder builder = getNotificationBuilder();
            builder
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle("True Name")
                    .setContentText("Identify unknown number");

            Notification notification = builder.build();

            startForeground(notificationId, notification);
        }

    }

    private void showHud() {
        if (ivCrumpledPaper != null) {
            mWindowManager.removeView(ivCrumpledPaper);
            ivCrumpledPaper = null;
        }

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mPaperParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displaymetrics);
        //   windowHeight = displaymetrics.heightPixels;
        windowWidth = displaymetrics.widthPixels;

        mPaperParams.gravity = Gravity.CENTER;
        mPaperParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;


        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (ptype == 0) {
            ivCrumpledPaper = li.inflate(R.layout.incoming_call_pop, null, false);
            txtName = ivCrumpledPaper.findViewById(R.id.txtName);
            AdView adView = ivCrumpledPaper.findViewById(R.id.adView);
            AdUtils.loadBannerAd(adView);
            TextView txtLastCall = ivCrumpledPaper.findViewById(R.id.txtLastCall);
            //txtLastCall.setText(TrueName.getLastCall(number,getApplicationContext()));
            txtName.setText(ComFunc.getContactName(number, this));

            txtAddress = ivCrumpledPaper.findViewById(R.id.txtAddress);
            txtNetwork = ivCrumpledPaper.findViewById(R.id.txtNetwork);
            recordCall(number);
            CallLogModel callLogModel = getCallLast(getApplicationContext(), number);
            if (callLogModel != null && !callLogModel.getCallDate().replace(" ", "").isEmpty())
                txtLastCall.setText(String.format("Last Call %s", getDate(Long.parseLong(callLogModel.getCallDate()), "dd/MM/yyyy hh:mm:ss")));
            getNumberDetails(number);
        } else if (ptype == 1) {

            ivCrumpledPaper = li.inflate(R.layout.after_call_pop, null, false);
            AdView adView = ivCrumpledPaper.findViewById(R.id.adView);
            AdUtils.loadBannerAd(adView);
            TextView txtLastCall = ivCrumpledPaper.findViewById(R.id.txtLastCall);
            btnMessage = ivCrumpledPaper.findViewById(R.id.btnMessage);
            btnCall = ivCrumpledPaper.findViewById(R.id.btnCall);
            btnInvite = ivCrumpledPaper.findViewById(R.id.btnInvite);
            btnSave = ivCrumpledPaper.findViewById(R.id.btnSave);
            mAdView = ivCrumpledPaper.findViewById(R.id.adView);
            txtName = ivCrumpledPaper.findViewById(R.id.txtName);
            txtName.setText(ComFunc.getContactName(number, this));
            txtAddress = ivCrumpledPaper.findViewById(R.id.txtAddress);
            txtNetwork = ivCrumpledPaper.findViewById(R.id.txtNetwork);
            ImageView ivWhatsApp = ivCrumpledPaper.findViewById(R.id.ivWta);
            Button ivLoc = ivCrumpledPaper.findViewById(R.id.ivLocation);

            ivWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactUtils.openWhatsAppChat(number);
                }
            });

            ivLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactUtils.shareLocationOnSms(number, txtName.getText().toString());
                }
            });
            CallLogModel callLogModel = getCallLast(getApplicationContext(), number);
            if (callLogModel != null && !callLogModel.getCallDate().replace(" ", "").isEmpty())
                txtLastCall.setText(String.format("Last Call %s", getDate(Long.parseLong(callLogModel.getCallDate()), "dd/MM/yyyy hh:mm:ss")));
            getNumberDetails(number);

            setupClick();
            initAds();
            showAds();

        } else if (ptype == 2) {
            ivCrumpledPaper = li.inflate(R.layout.invite_pop, null, false);
            Button btnInvite = ivCrumpledPaper.findViewById(R.id.btnInvite);
            TextView text = ivCrumpledPaper.findViewById(R.id.text);
            text.setText("Invite " + number + " to True Name to \n send instantly flash message and \n get missed call reminder");
            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS(number, "invitesms");
                    stopSelf();

                }
            });

        }
        try {

            txtNumber = ivCrumpledPaper.findViewById(R.id.txtNumber);
            ImageView cross = ivCrumpledPaper.findViewById(R.id.cross);
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSelf();
                }
            });
            txtNumber.setText(number);


            mPaperParams.x = Gravity.CENTER;
            mPaperParams.y = Gravity.CENTER;

            mWindowManager.addView(ivCrumpledPaper, mPaperParams);

            mPaperParams.flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

            addCrumpledPaperOnTouchListener();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupClick() {
        if (ptype == 1) {
            btnMessage.setOnClickListener(v -> {
                Uri uri = Uri.parse("smsto:" + number);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("sms_body", "The SMS text");
                startActivity(intent);
                stopSelf();
            });
            btnCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
            });
            btnInvite.setOnClickListener(v -> {
//                        Uri uri = Uri.parse("smsto:"+number);
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("sms_body", "Invite Text");
//                        startActivity(intent);

                final Intent i = getIntent(getApplicationContext());
                i.putExtra("number", number);
                i.putExtra("ptype", 2);
                startService(i);

            });
            btnSave.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
                startActivity(intent);
                stopSelf();
            });
        }
    }

    private void addCrumpledPaperOnTouchListener() {
        ivCrumpledPaper.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = mPaperParams.x;
                        initialY = mPaperParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastActionDownX = event.getX();
                        lastActionDownY = event.getY();

                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        // add recycle bin when moving crumpled paper
                        //addRecycleBinView();

                        return true;
                    case MotionEvent.ACTION_UP:

                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred

                        }


                        int centerOfScreenByX = windowWidth / 2;

                        // remove crumpled paper when the it is in the recycle bin's area


                        // always remove recycle bin ImageView when paper is dropped


                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // move paper ImageView


                        mPaperParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mPaperParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(ivCrumpledPaper, mPaperParams);
                        return true;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void getNumberDetails(String n) {
        Call<GetNumberResponse> call = apiInterface.getNumberDetails(n,"92");
        call.enqueue(new Callback<GetNumberResponse>() {
            @Override
            public void onResponse(@NotNull Call<GetNumberResponse> call, @NotNull Response<GetNumberResponse> response) {
                if (response.body() != null && response.body().getStatus()) {
                    GetNumberResponse.Data data  = response.body().getData();
                    txtName.setText(data.name);
                  //  Pixel.load() //todo add image here.
                    txtNumber.setText(data.number);
                } else {
                    Toast.makeText(getApplicationContext(), "Get Number API failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetNumberResponse> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (recorder != null) {
            RecordModel r = new RecordModel();
            r.setPath(filepath);
            CallLogModel c = getCallLast(getApplicationContext(), number);
            r.setRid(c.get_Id());
            r.save();
            recorder.stop();
        }*/
        recorder = null;
        // remove views on destroy!
        if (ivCrumpledPaper != null) {
            mWindowManager.removeView(ivCrumpledPaper);
            ivCrumpledPaper = null;
        }

        if (ivRecycleBin != null) {
            mWindowManager.removeView(ivRecycleBin);
            ivRecycleBin = null;
        }

        createAndShowForegroundNotification(true);
    }

    public String getNumber(String number) {
        if (number.length() == 13 && number.startsWith("+92")) {
            return number.replaceFirst("\\+", "");
        } else if (number.length() == 11 && number.startsWith("0")) {
            return number.replaceFirst("0", "92");
        } else if (number.length() == 12 && number.startsWith("92")) {
            return number;
        } else {
            return number;
        }
    }

    void showAds() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void initAds() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


            String deviceId = "84295E3E87EA7A5704A242894BA93601";

            //MobileAds.initialize(this,"ca-app-pub-7945535077164107~1299784514");

            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-7945535077164107/6944947141");
            AdRequest adRequest1 = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest1);
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // TODO Auto-generated method stub
                    super.onAdLoaded();
                    Toast.makeText(getApplicationContext(), "loaded", Toast.LENGTH_LONG).show();
                    interstitialAd.show();
                }
            });
            boolean isTestDevice = adRequest1.isTestDevice(this);

            // Toast.makeText(getApplicationContext(), "is Admob Test Device ? "+deviceId+" "+isTestDevice,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), "Invite SMS Sent", Toast.LENGTH_LONG).show();
    }

    private CallLogModel getCallLast(Context context, String numbers) {
        try {
            StringBuffer sb = new StringBuffer();

            //  String selection = CallLog.Calls.NUMBER + " = " + numbers;

            String[] strNumber = {numbers};
            CallLogModel callLogModelList = new CallLogModel();
            Uri contacts = CallLog.Calls.CONTENT_URI;
            @SuppressLint("MissingPermission")
            Cursor managedCursor = context.getContentResolver().query(contacts, null, CallLog.Calls.NUMBER + " = ? ", strNumber, "date asc");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
            int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
            sb.append("Call Details :");
            while (managedCursor.moveToNext()) {

                //   HashMap rowDataCall = new HashMap<String, String>();
                CallLogModel call = new CallLogModel();

                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                // long timestamp = convertDateToTimestamp(callDayTime);
                String callDuration = managedCursor.getString(duration);
                String simn = managedCursor.getString(sim);
                String sid = managedCursor.getString(id);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                    default:
                        dir = "OUTGOING";
                        break;

                }
                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");

                call.setCallType(dir);
                call.setCallDate(callDate);
                call.setPhNumber(phNumber);
                call.setCallDayTime(callDayTime);
                call.setSim(simn);
                call.set_Id(sid);
                int hours = Integer.valueOf(callDuration) / 3600;
                int minutes = (Integer.valueOf(callDuration) % 3600) / 60;
                int seconds = Integer.valueOf(callDuration) % 60;
                call.setCallDuration(String.format("%02d:%02d", minutes, seconds));

                // Uri allCalls = Uri.parse("content://call_log/calls");
                // Cursor c = ((MainActivity)getActivity()).managedQuery(allCalls, null, null, null, null);
                //String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
                //Log.d("sim",id);
                callLogModelList = call;


            }
            managedCursor.close();
            System.out.println(sb);
            return callLogModelList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void recordCall(String Number) {
        Date today = Calendar.getInstance().getTime();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String reportDate = formatter.format(today);
        String path = "/truename/recordings/" + Number;
        File storageDir = new File(Environment
                .getExternalStorageDirectory(), path);
        storageDir.mkdir();
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        filepath = storageDir.getAbsolutePath() + "/" + reportDate + ".wav";
        if (createDirIfNotExists(path)) {
            recorder.setOutputFile(filepath);
            try {
                recorder.prepare();
            } catch (java.io.IOException e) {
                recorder = null;
                e.printStackTrace();
                return;
            }
            recorder.start();
        }
    }


//    public void recordCall(String Number)
//    {
//        recorder = new MediaRecorder();
//        recorder.reset();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        Date today = Calendar.getInstance().getTime();
//        Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String reportDate = formatter.format(today);
//        String path="/truename/recordings/"+Number+"/"+reportDate+".wav";
//        if(createDirIfNotExists(path)) {
//            recorder.setOutputFile(path);
//            try {
//                recorder.prepare();
//                recorder.start();
//            } catch (java.io.IOException e) {
//                recorder = null;
//                e.printStackTrace();
//                return;
//            }
//
//        }
//    }
//
//    public static boolean createDirIfNotExists(String path) {
//        boolean ret = true;
//
//        File file = new File(Environment.getRootDirectory(), path);
//        if (!file.exists()) {
//            if (!file.mkdirs()) {
//                Log.e("TravellerLog :: ", "Problem creating Image folder");
//                ret = false;
//            }
//        }
//        return ret;
//    }
}
