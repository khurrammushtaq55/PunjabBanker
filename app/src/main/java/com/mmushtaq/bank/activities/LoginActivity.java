package com.mmushtaq.bank.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mmushtaq.bank.R;
import com.mmushtaq.bank.model.LoginModel;
import com.mmushtaq.bank.remote.AppConstants;
import com.mmushtaq.bank.remote.NetworkClient;
import com.mmushtaq.bank.remote.SharedPreferences;
import com.mmushtaq.bank.service.UserService;
import com.mmushtaq.bank.utils.BaseMethods;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mmushtaq.bank.remote.AppConstants.KEY_SHARED_PREFERENCE_LOGGED;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername;
    EditText edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( BaseMethods.Companion.isLogin() ) {
            if ( BaseMethods.Companion.haveNetworkConnection(this) )
                gotoBankActivity();
            else gotoCasesActivity();
        }   else {
            setContentView(R.layout.activity_login);

            HashSet<Integer> ab = new HashSet<>();
            ab.toArray();
            edtUsername = findViewById(R.id.input_email);
            edtPassword = findViewById(R.id.input_password);
            btnLogin = findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(v -> {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                //validate form
                if ( validate() ) {
                    //do login
                    doLogin(username, password);
                }

            });
        }
    }

    private void gotoBankActivity() {
        Intent intent = new Intent(LoginActivity.this, BanksListActivity.class);
        startActivity(intent);
        finish();
    }
    private void gotoCasesActivity() {
        Intent intent = new Intent(LoginActivity.this, CasesActivity.class);
        startActivity(intent);
        finish();
    }


    private void doLogin(final String username,final String password){
        BaseMethods.Companion.progressdialog(LoginActivity.this);
        UserService userService = NetworkClient.createService(UserService.class);
        Call<LoginModel> call = userService.login(username, password, "mobile");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, @NotNull Response response) {

                if(response.isSuccessful()){
                    LoginModel loginModel = (LoginModel) response.body();
                    String access_token = response.headers().get("access-token");
                    String client = response.headers().get("client");
                    String uid = response.headers().get("uid");
                    SharedPreferences.saveSharedPreference(AppConstants.KEY_PWD,password,LoginActivity.this);

                    SharedPreferences.saveSharedPreference("access-token",access_token,LoginActivity.this);
                    SharedPreferences.saveSharedPreference("client",client,LoginActivity.this);
                    SharedPreferences.saveSharedPreference("uid",uid,LoginActivity.this);

                    if(null!=loginModel  &&  loginModel.getStatus().equals("success")){
                        //login start main activity
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_FN, loginModel.getData().getFirst_name(), LoginActivity.this);
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_LN, loginModel.getData().getLast_name(), LoginActivity.this);

                        SharedPreferences.saveSharedPreference(KEY_SHARED_PREFERENCE_LOGGED, AppConstants.YES,LoginActivity.this);
                        gotoBankActivity();
                        if(loginModel.getData().isCan_upload_picture())
                        {
                            SharedPreferences.saveSharedPreference(AppConstants.KEY_CAN_UPLOAD_PICTURE,AppConstants.YES,LoginActivity.this);

                        }else
                        {
                            SharedPreferences.saveSharedPreference(AppConstants.KEY_CAN_UPLOAD_PICTURE,AppConstants.NO,LoginActivity.this);

                        }

                        SharedPreferences.saveSharedPreference(AppConstants.KEY_SUBMITTED_COUNT, String.valueOf(loginModel.getData().getSubmitted_cases_count()),LoginActivity.this);
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_PENDING_COUNT, String.valueOf(loginModel.getData().getPending_cases_count()),LoginActivity.this);


                    } else
                    if(null!=loginModel  &&  loginModel.getStatus().equals("fail")) {
                        Toast.makeText(LoginActivity.this, loginModel.getMessage() + " ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid login credentials. Please try again.", Toast.LENGTH_SHORT).show();
                }
                BaseMethods.Companion.finishprogress();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.Companion.finishprogress();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String email = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtUsername.setError("enter a valid email address");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 15) {
            edtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }

}
