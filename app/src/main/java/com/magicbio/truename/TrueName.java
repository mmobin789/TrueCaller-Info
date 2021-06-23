package com.magicbio.truename;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.webkit.WebView;

import androidx.room.Room;

import com.google.android.gms.ads.MobileAds;
import com.magicbio.truename.db.AppDatabase;
import com.magicbio.truename.observers.CallLogsObserver;
import com.magicbio.truename.observers.ContactsObserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ahmed Bilal on 12/8/2018.
 */

public class TrueName extends Application {

    private static TrueName instance;
    public AppDatabase appDatabase;

    public static TrueName getInstance() {
        return instance;
    }

    public static void saveUserId(Integer uid, Context context) {
        if (uid == null)
            return;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("uid", uid);
        editor.apply();
    }

    public static void saveLastUpdateTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("time", System.currentTimeMillis());
        editor.apply();
    }


    public static long getLastUpdateTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong("time", 0);
    }


    public static void setLastCall(String Number, Date time, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        editor.putString(Number, format.format(time));
        editor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("uid", -1);
    }

    public static void setContactsUploaded(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("up", true);
        editor.apply();
    }

    public static boolean areContactsUploaded(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("up", false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "trueCaller").allowMainThreadQueries().build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String process = getProcessName();
            if (!getPackageName().equals(process)) WebView.setDataDirectorySuffix(process);

        }
        MobileAds.initialize(this, initializationStatus -> Log.d("Ads", "Initialized"));
        //MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("A108FC51AC16CD61414B267DC4CAFB8B")).build());

        // printHashKey(this);
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new ContactsObserver(this));
        }

        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new CallLogsObserver());
        }

    }

 /*   private static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d(pContext.getString(R.string.app_name), "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
