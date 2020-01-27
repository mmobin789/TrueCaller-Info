package com.magicbio.truename.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;

public class SplashActivity extends AppCompatActivity {

    TextView btnGetStarted;


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
    }
}
