package com.mmushtaq.bank.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.mmushtaq.bank.R;
import com.mmushtaq.bank.adapter.BanksAdapter;
import com.mmushtaq.bank.interfaces.NetworkChangeListener;
import com.mmushtaq.bank.interfaces.ServerResponse;
import com.mmushtaq.bank.model.Case;
import com.mmushtaq.bank.model.CaseModel;
import com.mmushtaq.bank.receiver.NetworkChangeReceiver;
import com.mmushtaq.bank.remote.NetworkClient;
import com.mmushtaq.bank.remote.SharedPreferences;
import com.mmushtaq.bank.service.CaseVerificationService;
import com.mmushtaq.bank.utils.AppConstants;
import com.mmushtaq.bank.utils.BaseMethods;
import com.mmushtaq.bank.utils.CacheManager;
import com.mmushtaq.bank.utils.TinyDB;
import com.mmushtaq.bank.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BanksListActivity extends AppCompatActivity implements ServerResponse, NetworkChangeListener {

    private Button btnRefresh;
    private SharedViewModel sharedViewModel;
    private TinyDB tinyDB;
    private NetworkChangeReceiver networkChangeReceiver;
    private CaseModel offlineCasesResponseModel;
    private CaseModel caseResponseModel;
    private TextView pendingCasesCount;
    private TextView newCasesCount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_banklist);
        initUI();
        setDefaultUI();
        handleListener();


        if (BaseMethods.INSTANCE.haveNetworkConnection(this) && null == offlineCasesResponseModel) {
            //online case
            getCasesFromAPI();
        } else {
            //offline case
            getCasesFromDB();
        }
    }

    private void initUI() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btnRefresh = findViewById(R.id.btnRefresh);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        tinyDB = new TinyDB(this);
        sharedViewModel.serverResponse = this;
        offlineCasesResponseModel = tinyDB.getCasesResponseModel();

        // Initialize the BroadcastReceiver
        networkChangeReceiver = new NetworkChangeReceiver(this);
    }


    private void setDefaultUI() {
        pendingCasesCount = findViewById(R.id.pendingCasesCount);
        newCasesCount = findViewById(R.id.newCasesCount);

        pendingCasesCount.setText(String.format(SharedPreferences.getSharedPreferences(AppConstants.KEY_PENDING_COUNT, BanksListActivity.this)));
        newCasesCount.setText(String.format(SharedPreferences.getSharedPreferences(AppConstants.KEY_SUBMITTED_COUNT, BanksListActivity.this)));

        setOfflineCases();


    }

    private void setOfflineCases() {
        TextView offlineCasesCount = findViewById(R.id.offlineCasesCount);
        if (!tinyDB.getCasesArray(AppConstants.KEY_CASES).isEmpty()) {
            offlineCasesCount.setText(tinyDB.getCasesArray(AppConstants.KEY_CASES).size() + "");
        } else offlineCasesCount.setText("0");
    }

    private void getCasesFromDB() {
        if (null != offlineCasesResponseModel) {
            CacheManager.INSTANCE.setCaseResponseModel(offlineCasesResponseModel);
            setBanksView();
        }
    }

    private void handleListener() {
        btnRefresh.setOnClickListener(view -> {
            getCasesFromAPI();
            sendCasesToAPI();
        });
    }

    private void sendCasesToAPI() {
        if (!tinyDB.getCasesArray(AppConstants.KEY_CASES).isEmpty()) {
            sharedViewModel.saveCase(tinyDB.getCasesArray(AppConstants.KEY_CASES), SharedPreferences.getSharedPreferences("access-token", BanksListActivity.this),
                    SharedPreferences.getSharedPreferences("client", BanksListActivity.this),
                    SharedPreferences.getSharedPreferences("uid", BanksListActivity.this));
        }
    }

    private void setRefreshButton(boolean isVisible) {
        btnRefresh.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setBanksView() {

        RecyclerView bankRecyclerView = findViewById(R.id.bankRecyclerView);
        bankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BanksAdapter bankListAdapter = new BanksAdapter(this, processCases(Objects.requireNonNull(CacheManager.INSTANCE.getCaseResponseModel())));
        bankRecyclerView.setAdapter(bankListAdapter);

    }

    public static Map<String, List<Case>> processCases(CaseModel caseModel) {
        List<Case> cases = caseModel.getCases();
        Map<String, List<Case>> bankSchemeMap = new HashMap<>();

        for (Case caseItem : cases) {
            String bankName = caseItem.getBank_name();
            // Check if the bank name is already in the map
            if (bankSchemeMap.containsKey(bankName)) {
                // If bank name exists, add the scheme to its list
                bankSchemeMap.get(bankName).add(caseItem);
            } else {
                // If bank name does not exist, create a new list for it and add the scheme
                List<Case> schemeList = new ArrayList<>();
                schemeList.add(caseItem);
                bankSchemeMap.put(bankName, schemeList);
            }
        }

        return bankSchemeMap;
    }



    public void getCasesFromAPI() {

        BaseMethods.INSTANCE.showProgressDialog(this);
        CaseVerificationService caseVerificationService = NetworkClient.createService(CaseVerificationService.class);
        Call<CaseModel> call = caseVerificationService.getCases(SharedPreferences.getSharedPreferences("access-token", this), SharedPreferences.getSharedPreferences("client", this), SharedPreferences.getSharedPreferences("uid", this), "*/*");
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull retrofit2.Response response) {

                BaseMethods.INSTANCE.hideProgressDialog();
                if (response.isSuccessful()) {

                    caseResponseModel = (CaseModel) response.body();
                    if (null != caseResponseModel && caseResponseModel.getStatus().equals("success")) {
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_SUBMITTED_COUNT, String.valueOf(caseResponseModel.getSubmitted_cases_count()), BanksListActivity.this);
                        SharedPreferences.saveSharedPreference(AppConstants.KEY_PENDING_COUNT, String.valueOf(caseResponseModel.getPending_cases_count()), BanksListActivity.this);
                        pendingCasesCount.setText(String.format(SharedPreferences.getSharedPreferences(AppConstants.KEY_PENDING_COUNT, BanksListActivity.this)));
                        newCasesCount.setText(String.format(SharedPreferences.getSharedPreferences(AppConstants.KEY_SUBMITTED_COUNT, BanksListActivity.this)));
                        //adding in cache
                        CacheManager.INSTANCE.setCaseResponseModel(caseResponseModel);
                        //adding in local database
                        tinyDB.putCasesResponseModel(caseResponseModel);
                        setBanksView();

                    } else {
                        Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                    Log.e("error", response.toString());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.INSTANCE.hideProgressDialog();
                Toast.makeText(BanksListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_logout, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void showResetActivity() {
        Intent intent = new Intent(BanksListActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void showLogoutAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BanksListActivity.this).setTitle("Alert").setMessage(getString(R.string.want_to_logout)).setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            dialogInterface.dismiss();
            logout();
        }).setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void logout() {
        BaseMethods.INSTANCE.showProgressDialog(this);

        CaseVerificationService caseVerificationService = NetworkClient.createService(CaseVerificationService.class);
        Call call = caseVerificationService.sign_out(SharedPreferences.getSharedPreferences("access-token", this), SharedPreferences.getSharedPreferences("client", this), SharedPreferences.getSharedPreferences("uid", this));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                BaseMethods.INSTANCE.hideProgressDialog();
                if (response.isSuccessful()) {
                    gotoLoginActivity();
                } else
                    Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                BaseMethods.INSTANCE.hideProgressDialog();
                Toast.makeText(BanksListActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void gotoLoginActivity() {
        SharedPreferences.saveSharedPreference(AppConstants.KEY_SHARED_PREFERENCE_LOGGED, AppConstants.NO, getApplicationContext());
        tinyDB.clear();
        finish();
        startActivity(new Intent(BanksListActivity.this, LoginActivity.class));
    }

    public void goToSchemas(List<Case> schemedCases) {

        CacheManager.INSTANCE.setSchemedCases(schemedCases);
        startActivity(new Intent(BanksListActivity.this, SchemeActivity.class));

    }

    @Override
    public void onSuccess(@NonNull Response<CaseModel> message) {
        tinyDB.remove(AppConstants.KEY_CASES);
        setOfflineCases();

    }

    @Override
    public void onFailure(@Nullable String message) {
        BaseMethods.INSTANCE.hideProgressDialog();
    }

    @Override
    public void onCaseSuccess(@NonNull Response<CaseModel> message) {
        BaseMethods.INSTANCE.hideProgressDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkAvailable() {
        setRefreshButton(true);
    }

    @Override
    public void onNetworkUnavailable() {
        setRefreshButton(false);
    }
}
