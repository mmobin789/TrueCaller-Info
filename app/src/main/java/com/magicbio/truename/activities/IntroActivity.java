package com.magicbio.truename.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hbb20.CountryCodePicker;
import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;
import com.magicbio.truename.adapters.ViewPagerAdapter;
import com.magicbio.truename.fragments.background.AppAsyncWorker;
import com.magicbio.truename.models.SignUpResponse;
import com.magicbio.truename.retrofit.ApiClient;
import com.magicbio.truename.retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));


        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }


    }

    public void showLoginPopUp() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login);
        final EditText etPhone = dialog.findViewById(R.id.etPhone);
        final CountryCodePicker ccp = dialog.findViewById(R.id.ccp);
        Button loginBtn = dialog.findViewById(R.id.login);
        ccp.registerCarrierNumberEditText(etPhone);
        loginBtn.setOnClickListener(v -> {
            String phoneNumber = etPhone.getText().toString();
            String countryCode = ccp.getSelectedCountryCode();
            if (ccp.isValidFullNumber())
                login(phoneNumber, countryCode, dialog);
            else etPhone.setError("Required Valid Phone Number");
        });

        dialog.show();
    }


    private void login(String number, String countryCode, final Dialog dialog) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);


        Call<SignUpResponse> call = apiInterface.signup(number, countryCode);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(@NotNull Call<SignUpResponse> call, @NotNull Response<SignUpResponse> response) {
                if (response.body() != null && response.body().getStatus()) {
                    int userId = response.body().getId();
                    TrueName.saveUserId(userId, IntroActivity.this);
                    dialog.dismiss();
                    progressDialog.dismiss();
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    AppAsyncWorker.saveContactsToDb(apiInterface, userId);
                    AppAsyncWorker.saveCallLogToDb();
                }


            }

            @Override
            public void onFailure(@NotNull Call<SignUpResponse> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(IntroActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void gotoNext() {
        viewPager.setCurrentItem(1, true);
    }


}
