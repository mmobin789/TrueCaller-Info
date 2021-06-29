package com.magicbio.truename.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ImageView btnGetStarted;
    private final ArrayList<String> permissions = new ArrayList<>(10);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        if (TrueName.getUserId(this) == -1) {
            btnGetStarted.setOnClickListener(v -> {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            });

        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        takePermissions();

    }

    private boolean notHasPermission(String permission) {
        return checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void takePermissions() {


        if (notHasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (notHasPermission(Manifest.permission.INTERNET)) {
            permissions.add(Manifest.permission.INTERNET);
        }

        if (notHasPermission(Manifest.permission.READ_CALL_LOG)) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }

        if (notHasPermission(Manifest.permission.READ_CONTACTS)) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (notHasPermission(Manifest.permission.SEND_SMS)) {
            permissions.add(Manifest.permission.SEND_SMS);
        }

        if (notHasPermission(Manifest.permission.READ_PHONE_STATE)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notHasPermission(Manifest.permission.READ_PHONE_NUMBERS)) {
            permissions.add(Manifest.permission.READ_PHONE_NUMBERS);
        }

        if (notHasPermission(Manifest.permission.CALL_PHONE)) {
            permissions.add(Manifest.permission.CALL_PHONE);

        }

        if (notHasPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && notHasPermission(Manifest.permission.FOREGROUND_SERVICE)) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }


        if (permissions.isEmpty()) {
            return;
        }


        String[] array = permissions.toArray(new String[]{});
        requestPermissions(array, 3);

    }

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == 3 && permissions.length == this.permissions.size()) {
//todo
        }
    }*/
/* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContactUtils.handleFacebookResult(requestCode, resultCode, data);
    }*/
}
