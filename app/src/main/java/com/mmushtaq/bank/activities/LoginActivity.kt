package com.mmushtaq.bank.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mmushtaq.bank.R
import com.mmushtaq.bank.model.LoginModel
import com.mmushtaq.bank.remote.NetworkClient
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.service.CaseVerificationService
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods.hideProgressDialog
import com.mmushtaq.bank.utils.BaseMethods.isLogin
import com.mmushtaq.bank.utils.BaseMethods.showProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var edtUsername: EditText? = null
    private var edtPassword: EditText? = null
    private var btnLogin: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLogin(this@LoginActivity)) {
            gotoBankActivity()
        } else {
            setContentView(R.layout.activity_login)
            edtUsername = findViewById(R.id.input_email)
            edtPassword = findViewById(R.id.input_password)
            btnLogin = findViewById(R.id.btn_login)
            //            edtUsername.setText("test@hspl.com");
            //            edtPassword.setText("carrot123@Qs");
            btnLogin?.setOnClickListener(View.OnClickListener {
                val username = edtUsername?.text.toString()
                val password = edtPassword?.text.toString()
                //validate form
                if (validate()) {
                    //do login
                    doLogin(username, password)
                }
            })
        }
    }

    private fun gotoBankActivity() {
        val intent = Intent(this@LoginActivity, BanksListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun doLogin(username: String, password: String) {
        showProgressDialog(this@LoginActivity)
        val caseVerificationService = NetworkClient.createService(
            CaseVerificationService::class.java
        )
        val call = caseVerificationService.login(username, password, "mobile")
        call.enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                if (response.isSuccessful) {
                    val loginModel = response.body()
                    val accessToken = response.headers()["access-token"]
                    val client = response.headers()["client"]
                    val uid = response.headers()["uid"]
                    SharedPreferences.saveSharedPreference(
                        AppConstants.KEY_PWD,
                        password,
                        this@LoginActivity
                    )
                    SharedPreferences.saveSharedPreference(
                        "access-token",
                        accessToken,
                        this@LoginActivity
                    )
                    SharedPreferences.saveSharedPreference("client", client, this@LoginActivity)
                    SharedPreferences.saveSharedPreference("uid", uid, this@LoginActivity)
                    if (null != loginModel && loginModel.status == "success") {
                        //login start main activity
                        SharedPreferences.saveSharedPreference(
                            AppConstants.KEY_FN,
                            loginModel.data.first_name,
                            this@LoginActivity
                        )
                        SharedPreferences.saveSharedPreference(
                            AppConstants.KEY_LN,
                            loginModel.data.last_name,
                            this@LoginActivity
                        )
                        SharedPreferences.saveSharedPreference(
                            AppConstants.KEY_SHARED_PREFERENCE_LOGGED,
                            AppConstants.YES,
                            this@LoginActivity
                        )
                        if (loginModel.data.isCan_upload_picture) {
                            SharedPreferences.saveSharedPreference(
                                AppConstants.KEY_CAN_UPLOAD_PICTURE,
                                AppConstants.YES,
                                this@LoginActivity
                            )
                        } else {
                            SharedPreferences.saveSharedPreference(
                                AppConstants.KEY_CAN_UPLOAD_PICTURE,
                                AppConstants.NO,
                                this@LoginActivity
                            )
                        }
                        SharedPreferences.saveSharedPreference(
                            AppConstants.KEY_SUBMITTED_COUNT,
                            loginModel.data.submitted_cases_count.toString(),
                            this@LoginActivity
                        )
                        SharedPreferences.saveSharedPreference(
                            AppConstants.KEY_PENDING_COUNT,
                            loginModel.data.pending_cases_count.toString(),
                            this@LoginActivity
                        )
                        gotoBankActivity()
                    } else if (null != loginModel && loginModel.status == "fail") {
                        Toast.makeText(
                            this@LoginActivity,
                            loginModel.getMessage() + " ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid login credentials. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressDialog()
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                hideProgressDialog()
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validate(): Boolean {
        var valid = true
        val email = edtUsername?.text.toString()
        val password = edtPassword?.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtUsername?.error = "enter a valid email address"
            valid = false
        } else {
            edtUsername?.error = null
        }
        if (password.isEmpty() || password.length < 4 || password.length > 15) {
            edtPassword?.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            edtPassword?.error = null
        }
        return valid
    }
}
