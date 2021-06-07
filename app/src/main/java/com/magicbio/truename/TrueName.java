package com.magicbio.truename;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import androidx.room.Room;

import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.magicbio.truename.db.AppDatabase;
import com.magicbio.truename.observers.CallLogsObserver;
import com.magicbio.truename.observers.ContactsObserver;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ahmed Bilal on 12/8/2018.
 */

public class TrueName extends Application {

    private static TrueName instance;
    private static final Gson gson = new Gson();
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

    public static void setLastCall(String Number, Date time, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        editor.putString(Number, format.format(time));
        editor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("uid", -1);
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

        MobileAds.initialize(this, getString(R.string.adMob_ID));
        // printHashKey(this);
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new ContactsObserver(this));
        }

        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new CallLogsObserver());
        }

    }

    private static void printHashKey(Context pContext) {
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
    }
}
