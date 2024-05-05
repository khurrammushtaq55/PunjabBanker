package com.mmushtaq.bank.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mmushtaq.bank.R;
import com.mmushtaq.bank.model.LoginModel;
import com.mmushtaq.bank.remote.BaseApplication;
import com.mmushtaq.bank.remote.NetworkClient;
import com.mmushtaq.bank.remote.SharedPreferences;
import com.mmushtaq.bank.service.CaseVerificationService;
import com.mmushtaq.bank.utils.AppConstants;
import com.mmushtaq.bank.utils.BaseMethods;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtOldPwd;
    EditText edtNewPwd;
    EditText edtCnfrmPwd;
    Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        edtOldPwd = findViewById(R.id.old_password);
        edtNewPwd = findViewById(R.id.new_password);
        edtCnfrmPwd = findViewById(R.id.cnfrm_password);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> {
            String oldPassword = edtOldPwd.getText().toString();
            String newPassword = edtNewPwd.getText().toString();
            String cnfrmPassword = edtCnfrmPwd.getText().toString();
            //validate form
            if (validate()) {
                //do login
                doReset(oldPassword, newPassword);
            }

        });
    }


    private void doReset(final String username, final String password) {
        BaseMethods.INSTANCE.showProgressDialog(ResetPasswordActivity.this);
        CaseVerificationService caseVerificationService = NetworkClient.createService(CaseVerificationService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("user[first_name]", SharedPreferences.getSharedPreferences(AppConstants.KEY_FN, this));
        map.put("user[last_name]", SharedPreferences.getSharedPreferences(AppConstants.KEY_LN, this));
        map.put("user[password]", password);
        Call<LoginModel> call = caseVerificationService.reset(SharedPreferences.getSharedPreferences("access-token", this),
                SharedPreferences.getSharedPreferences("client", this),
                SharedPreferences.getSharedPreferences("uid", this), map);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    LoginModel loginModel = (LoginModel) response.body();

                    if (null != loginModel && loginModel.getStatus().equals("success")) {

                        logout();
                    } else if (null != loginModel && loginModel.getStatus().equals("fail")) {
                        Toast.makeText(ResetPasswordActivity.this, loginModel.getMessage() + " ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                BaseMethods.INSTANCE.hideProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.INSTANCE.hideProgressDialog();
                Toast.makeText(ResetPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        BaseMethods.INSTANCE.showProgressDialog(this);

        CaseVerificationService caseVerificationService = NetworkClient.createService(CaseVerificationService.class);
        Call call = caseVerificationService.sign_out(SharedPreferences.getSharedPreferences("access-token", this),
                SharedPreferences.getSharedPreferences("client", this),
                SharedPreferences.getSharedPreferences("uid", this));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                BaseMethods.INSTANCE.hideProgressDialog();
                if (response.isSuccessful()) {
                    SharedPreferences.saveSharedPreference(AppConstants.KEY_SHARED_PREFERENCE_LOGGED,
                            AppConstants.NO, ResetPasswordActivity.this);
                    finish();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                } else
                    Toast.makeText(ResetPasswordActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.INSTANCE.hideProgressDialog();
                Toast.makeText(ResetPasswordActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        String oldPassword = edtOldPwd.getText().toString();
        String newPassword = edtNewPwd.getText().toString();
        String cfmPassword = edtCnfrmPwd.getText().toString();

        if (oldPassword.isEmpty() || oldPassword.length() < 4 || oldPassword.length() > 15) {
            edtOldPwd.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!oldPassword.equals(SharedPreferences.getSharedPreferences(AppConstants.KEY_PWD, this))) {
            {
                edtOldPwd.setError("wrong password");
                valid = false;
            }
        } else {
            edtOldPwd.setError(null);
        }

        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 15) {
            edtNewPwd.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtNewPwd.setError(null);
        }

        if (cfmPassword.isEmpty() || cfmPassword.length() < 4 || cfmPassword.length() > 15) {
            edtCnfrmPwd.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtCnfrmPwd.setError(null);
            if (!newPassword.isEmpty() && !newPassword.equals(cfmPassword)) {
                edtCnfrmPwd.setError("Passwords are not same");
                valid = false;
            }
        }


        return valid;
    }

}
