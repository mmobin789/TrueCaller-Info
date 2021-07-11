package com.magicbio.truename.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;

public class SplashActivity extends AppCompatActivity {

    ImageView btnGetStarted;


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


    }
}
