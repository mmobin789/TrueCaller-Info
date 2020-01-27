package com.magicbio.truename.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;
import com.magicbio.truename.R;
import com.magicbio.truename.TrueName;
import com.magicbio.truename.models.SignUpResponse;
import com.magicbio.truename.retrofit.ApiClient;
import com.magicbio.truename.retrofit.ApiInterface;

import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    public static int APP_REQUEST_CODE = 90;
    Button btnContinune;
    EditText txtFname, txtLname, txtEmail, txtAddress, txtCity;
    ApiInterface apiInterface;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Profile profile;
    ProgressDialog progressDoalog;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txtFname = findViewById(R.id.txtFname);
        txtLname = findViewById(R.id.txtLname);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddress = findViewById(R.id.txtAddress);
        txtCity = findViewById(R.id.txtCity);
        btnContinune = findViewById(R.id.btnContinune);
        loginButton = findViewById(R.id.btnFacebook);
        progressDoalog = new ProgressDialog(SignUpActivity.this);
        progressDoalog.setMessage("Please Wait.....");
        progressDoalog.setCancelable(false);
        progressDoalog.setIndeterminate(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        profile = Profile.getCurrentProfile();


        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                loginResult.toString();


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {

                                if (com.facebook.AccessToken.getCurrentAccessToken() != null) {

                                    if (me != null) {

                                        new GraphRequest(
                                                loginResult.getAccessToken(),
                                                me.optString("id") + "/",
                                                null,
                                                HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    public void onCompleted(GraphResponse response) {
                                                        String email = response.getRawResponse();
                                                        Log.d("all data", email);
                                                        /* handle the result */
                                                    }
                                                }
                                        ).executeAsync();

                                        String profileImageUrl = ImageRequest.getProfilePictureUri(me.optString("id"), 500, 500).toString();
                                        Log.i("profile image", profileImageUrl);

                                    }
                                }
                            }
                        });
                GraphRequest.executeBatchAsync(request);

//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender,birthday");
//                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
//                        loginResult.getAccessToken(),
//                        //AccessToken.getCurrentAccessToken(),
//                        "/me",
//                        null,
//                        HttpMethod.GET,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
////                                try {
//                                   // JSONArray rawName = response.getJSONObject().getJSONArray("data");
//                                    String email=response.getRawResponse().toString();
//                                   // Log.d("rawName friendList",String.valueOf(rawName));
//                                    Log.d("all data",email);
//                                    com.facebook.AccessToken token = com.facebook.AccessToken.getCurrentAccessToken();
//                                    Log.d("access token is: ",String.valueOf(token));
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
//                            }
//                        }
//                ).executeAsync();
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        txtFname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtFname.getText().length() == 0) {
                    txtFname.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.name), null, getResources().getDrawable(R.drawable.cross), null);
                } else {
                    txtFname.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.name), null, getResources().getDrawable(R.drawable.tick), null);
                }

            }
        });


        txtLname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtLname.getText().length() == 0) {
                    txtLname.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.name), null, getResources().getDrawable(R.drawable.cross), null);
                } else {
                    txtLname.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.name), null, getResources().getDrawable(R.drawable.tick), null);
                }

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtEmail.getText().length() == 0 || !isValidEmail(txtEmail.getText())) {
                    txtEmail.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.email), null, getResources().getDrawable(R.drawable.cross), null);
                } else {
                    txtEmail.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.email), null, getResources().getDrawable(R.drawable.tick), null);
                }

            }
        });

        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtAddress.getText().length() == 0) {
                    txtAddress.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.address), null, getResources().getDrawable(R.drawable.cross), null);
                } else {
                    txtAddress.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.address), null, getResources().getDrawable(R.drawable.tick), null);
                }

            }
        });

        txtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtCity.getText().length() == 0) {
                    txtCity.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.city), null, getResources().getDrawable(R.drawable.cross), null);
                } else {
                    txtCity.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.city), null, getResources().getDrawable(R.drawable.tick), null);
                }

            }
        });
        btnContinune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid(txtFname) && isValid(txtLname) && isValidEmail(txtEmail.getText().toString()) && isValid(txtAddress) && isValid(txtCity)) {
                    numberVerification();
                } else {
                    Toast.makeText(getApplicationContext(), "all fields required", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean isValid(TextView t) {
        if (t.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), t.getText().toString(), Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void numberVerification() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();


        if (accessToken != null) {
            phoneLogin(btnContinune);
        } else {
            phoneLogin(btnContinune);

        }
    }

    public void phoneLogin(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //// showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        String accountKitId = account.getId();
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        //Toast.makeText(getApplicationContext(),phoneNumber.toString(),Toast.LENGTH_LONG).show();
                        String phoneNumberString = phoneNumber.toString().substring(1);
                        login(phoneNumberString);
                        Toast.makeText(getApplicationContext(), phoneNumberString, Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        Log.e("error", error.toString());
                    }
                });

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //goToMyLoggedInActivity();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void login(String phone) {
        progressDoalog.show();
        Call<SignUpResponse> call = apiInterface.signup("signup", EtoStr(txtFname) + " " + EtoStr(txtLname), EtoStr(txtEmail), EtoStr(txtAddress), EtoStr(txtCity), "Pakistan", phone);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                progressDoalog.dismiss();
                TrueName.setIsLogin(true, getApplicationContext());
                TrueName.SaveUserInfo(response.body().getInfo(), getApplicationContext());
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), response.body().getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {

            }
        });


    }


    public String EtoStr(EditText e) {
        return e.getText().toString();
    }


}
