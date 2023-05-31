package com.mmushtaq.bank.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.mmushtaq.bank.R;
import com.mmushtaq.bank.adapter.BanksAdapter;
import com.mmushtaq.bank.model.Banks;
import com.mmushtaq.bank.model.CaseModel;
import com.mmushtaq.bank.remote.AppConstants;
import com.mmushtaq.bank.remote.BaseApplication;
import com.mmushtaq.bank.remote.NetworkClient;
import com.mmushtaq.bank.remote.SharedPreferences;
import com.mmushtaq.bank.service.UserService;
import com.mmushtaq.bank.utils.BaseMethods;
import com.mmushtaq.bank.utils.CacheManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BanksListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_banklist);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

           List<Banks> banksList=new ArrayList<>();
           banksList.add(new Banks("BANK OF PUNJAB"));

           RecyclerView bankRecyclerView=findViewById(R.id.bankRecyclerView);
           TextView pendingCasesCount=findViewById(R.id.pendingCasesCount);
           TextView newCasesCount=findViewById(R.id.newCasesCount);
           bankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
           BanksAdapter bankListAdapter=new BanksAdapter(this,banksList);
           bankRecyclerView.setAdapter(bankListAdapter);


           pendingCasesCount.setText(SharedPreferences.getSharedPreferences(AppConstants.KEY_PENDING_COUNT,BanksListActivity.this));
           newCasesCount.setText(SharedPreferences.getSharedPreferences(AppConstants.KEY_SUBMITTED_COUNT,BanksListActivity.this));



    }
    CaseModel caseModel;

    public void getCases(){

        BaseMethods.Companion.progressdialog(this);
        UserService userService= NetworkClient.createService(UserService.class);
        Call<CaseModel> call = userService.getCases(SharedPreferences.getSharedPreferences("access-token", getApplicationContext()),
                SharedPreferences.getSharedPreferences("client", BaseApplication.getContext())
        ,SharedPreferences.getSharedPreferences("uid", BaseApplication.getContext()),"*/*");
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, retrofit2.Response response) {

                BaseMethods.Companion.finishprogress();
                if(response.isSuccessful()){

                    caseModel = (CaseModel) response.body();
                    if(null!=caseModel  &&  caseModel.getStatus().equals("success")) {
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_SUBMITTED_COUNT, String.valueOf(caseModel.getSubmitted_cases_count()), BanksListActivity.this);
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_PENDING_COUNT, String.valueOf(caseModel.getPending_cases_count()), BanksListActivity.this);

                        //login start main activity
                        Intent intent = new Intent(BanksListActivity.this, CasesActivity.class);
//                        intent.putExtra(AppConstants.KEY_CASES_ARRAY, caseModel);
                        CacheManager.INSTANCE.getInstance().setCaseModel(caseModel);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                    Log.e("errror",response.toString());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.Companion.finishprogress();
                Toast.makeText(BanksListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.item1) {

            showResetActivity();
            return true;
        }
        if (item.getItemId() == R.id.item2) {

            showLogoutAlert();
            return true;
        }
        if (item.getItemId() == R.id.item3) {

//            showResetActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showResetActivity() {
        Intent intent=new Intent(BanksListActivity.this,ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void showLogoutAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BanksListActivity.this)
                .setTitle("Alert").setMessage(getString(R.string.want_to_logout)).setPositiveButton(getString(R.string.yes),
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            logout();
                        }).setNegativeButton(android.R.string.no,
                        (dialogInterface, i) -> dialogInterface.dismiss());
         alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void logout() {
        BaseMethods.Companion.progressdialog(this);

        UserService userService = NetworkClient.createService(UserService.class);
        Call call = userService.sign_out(SharedPreferences.getSharedPreferences("access-token", BaseApplication.getContext()),
                SharedPreferences.getSharedPreferences("client", BaseApplication.getContext()),
                SharedPreferences.getSharedPreferences("uid", BaseApplication.getContext()));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                BaseMethods.Companion.finishprogress();
                if(response.isSuccessful()){
                    SharedPreferences.saveSharedPreference(AppConstants.KEY_SHARED_PREFERENCE_LOGGED,
                            AppConstants.NO, BaseApplication.getContext());
                    finish();
                    startActivity(new Intent(BanksListActivity.this, LoginActivity.class));
                }else
                    Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.Companion.finishprogress();
                Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
