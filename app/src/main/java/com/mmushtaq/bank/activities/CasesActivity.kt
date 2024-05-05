package com.mmushtaq.bank.activities

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gu.toolargetool.TooLargeTool
import com.mmushtaq.bank.R
import com.mmushtaq.bank.adapter.CasesAdapter
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.remote.NetworkClient
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.service.CaseVerificationService
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager
import com.mmushtaq.bank.utils.TinyDB
import kotlinx.android.synthetic.main.activity_cases_list.emptyListView
import kotlinx.android.synthetic.main.activity_cases_list.etSearchCases
//import kotlinx.android.synthetic.main.activity_cases_list.syncButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CasesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.clear();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cases_list)
        TooLargeTool.startLogging(applicationContext as Application)
            val tinyDB = TinyDB(this)
            if (null != tinyDB.getCaseModel(AppConstants.KEY_ALL_CASES)) {

                CacheManager.caseModel = tinyDB.getCaseModel(AppConstants.KEY_ALL_CASES)!!
            }
        setAdapter(CacheManager.schemedCases)

    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.clear()
        super.onSaveInstanceState(outState, outPersistentState)

    }

    private fun setAdapter(cases: List<Case>?) {
        if (cases?.size!! > 0) {
            emptyListView.visibility = View.GONE
            val bankRecyclerView = findViewById<RecyclerView>(R.id.bankUsersRecyclerView)
            bankRecyclerView.layoutManager = LinearLayoutManager(this)
            val casesAdapter = CasesAdapter(this, cases as ArrayList<Case>)
            bankRecyclerView.adapter = casesAdapter

            etSearchCases.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val text = etSearchCases.text.toString().toLowerCase(Locale.ROOT)
                    casesAdapter.filter(text);
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

        } else emptyListView.visibility = View.VISIBLE

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.item1) {
            showHomeAlert()
            return true
        }
        if (id == R.id.item2) {
            showResetActivity()
            return true
        }
        if (id == R.id.item3) {

            showLogoutAlert()
            return true
        }


        return super.onOptionsItemSelected(item)

    }

    private fun showResetActivity() {
        val intent = Intent(this@CasesActivity, ResetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showLogoutAlert() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Alert")
            setMessage(getString(R.string.want_to_logout))
            setPositiveButton(
                getString(R.string.yes),
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton(android.R.string.no, negativeButtonClick)
            show()
        }


    }

    private val positiveButtonClick = { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
        logout()
    }
    private val negativeButtonClick = { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
    }


    private fun showHomeAlert() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Alert")
            setMessage(getString(R.string.goto_homescreen))
            setPositiveButton(
                getString(R.string.yes),
                DialogInterface.OnClickListener(function = positiveHomeButtonClick)
            )
            setNegativeButton(android.R.string.no, negativeButtonClick)
            show()
        }


    }

    private val positiveHomeButtonClick = { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
        finishAffinity()
        val intent = Intent(this, BanksListActivity::class.java)
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }


    private fun logout() {
        BaseMethods.showProgressDialog(this)

        val caseVerificationService =
            NetworkClient.createService(CaseVerificationService::class.java)
        val call = caseVerificationService.sign_out(
            SharedPreferences.getSharedPreferences("access-token", applicationContext),
            SharedPreferences.getSharedPreferences("client", applicationContext),
            SharedPreferences.getSharedPreferences("uid", applicationContext)
        )
        call.enqueue(object : Callback<CaseModel> {
            override fun onResponse(call: Call<CaseModel>, response: Response<CaseModel>) {
                BaseMethods.hideProgressDialog()
                if (response.isSuccessful) {
                    SharedPreferences.saveSharedPreference(
                        AppConstants.KEY_SHARED_PREFERENCE_LOGGED,
                        AppConstants.NO,
                        applicationContext
                    )
                    finish()
                    startActivity(Intent(this@CasesActivity, LoginActivity::class.java))
                } else
                    Toast.makeText(
                        this@CasesActivity,
                        "Error! Please try again!",
                        Toast.LENGTH_SHORT
                    ).show()

            }

            override fun onFailure(call: Call<CaseModel>, t: Throwable) {
                BaseMethods.hideProgressDialog()
                Toast.makeText(this@CasesActivity, "Error! Please try again!", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }


    override fun onBackPressed() {

        if (!this@CasesActivity.supportFragmentManager.popBackStackImmediate()) {
            showHomeAlert()
        }
    }

    override fun onResume() {
        super.onResume()
        this@CasesActivity.supportFragmentManager.isStateSaved
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.clear()
        super.onRestoreInstanceState(savedInstanceState)
    }

}