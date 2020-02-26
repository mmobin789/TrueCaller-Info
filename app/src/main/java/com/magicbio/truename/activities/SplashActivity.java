package com.magicbio.truename.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;
import com.magicbio.truename.fragments.background.AppAsyncWorker;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ImageView btnGetStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btnGetStarted = findViewById(R.id.btnGetStarted);

        if (!TrueName.getIslogin(getApplicationContext())) {
            btnGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(intent);
                    finish();
                }
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
        ArrayList<String> permissions = new ArrayList<>(9);

        if (notHasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (notHasPermission(Manifest.permission.READ_CALL_LOG)) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        } else AppAsyncWorker.fetchCallLog(null);

        if (notHasPermission(Manifest.permission.WRITE_CALL_LOG)) {
            permissions.add(Manifest.permission.WRITE_CALL_LOG);
        }

        if (notHasPermission(Manifest.permission.READ_CONTACTS)) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (notHasPermission(Manifest.permission.READ_SMS)) {
            permissions.add(Manifest.permission.READ_SMS);
        }


        if (notHasPermission(Manifest.permission.SEND_SMS)) {
            permissions.add(Manifest.permission.SEND_SMS);
        }

        if (notHasPermission(Manifest.permission.READ_PHONE_STATE)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (notHasPermission(Manifest.permission.RECORD_AUDIO)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (notHasPermission(Manifest.permission.CALL_PHONE)) {
            permissions.add(Manifest.permission.CALL_PHONE);

        }

        if (permissions.isEmpty())
            return;

        String[] array = permissions.toArray(new String[]{});
        requestPermissions(array, 3);

    }
}
