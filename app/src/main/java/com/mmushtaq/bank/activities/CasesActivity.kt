package com.mmushtaq.bank.activities

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gu.toolargetool.TooLargeTool
import com.mmushtaq.bank.R
import com.mmushtaq.bank.adapter.CasesAdapter
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.remote.AppConstants
import com.mmushtaq.bank.remote.BaseApplication
import com.mmushtaq.bank.remote.NetworkClient
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.service.UserService
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager
import com.mmushtaq.bank.utils.TinyDB
import com.mmushtaq.bank.viewmodel.SectionsViewModel
import com.mmushtaq.bank.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.activity_bank_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CasesActivity : AppCompatActivity(), SectionsViewModel.ServerResponse {
    private lateinit var viewModel: SectionsViewModel
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.clear();
        super.onCreate(savedInstanceState)
//        CacheManager.instance?.caseModel
        setContentView(R.layout.activity_bank_users)
        TooLargeTool.startLogging(applicationContext as Application)
        viewModel = ViewModelProvider(this).get(SectionsViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        viewModel.serverResponse = this

        if (!BaseMethods.haveNetworkConnection(this)) {
            val tinyDB = TinyDB(this)
            if (null != tinyDB.getCaseObject(AppConstants.KEY_ALL_CASES)) {

                CacheManager.instance?.caseModel = tinyDB.getCaseObject(AppConstants.KEY_ALL_CASES)!!
            }
        } else if (BaseMethods.haveNetworkConnection(this)) {
            val tinyDB = TinyDB(this)
            if (null != tinyDB.getObject(AppConstants.KEY_CASES) && !tinyDB.getObject(AppConstants.KEY_CASES).isNullOrEmpty()) {
                sync.visibility = View.VISIBLE
            } else {
                sync.visibility = View.GONE
            }
            sync.setOnClickListener {
                BaseMethods.progressdialog(this)
                viewModel.saveCase(tinyDB.getObject(AppConstants.KEY_CASES)!!)
            }


            val intent = intent
//            CacheManager.instance?.caseModel = intent.getSerializableExtra(AppConstants.KEY_CASES_ARRAY) as CaseModel?
            tinyDB.putCaseObject(AppConstants.KEY_ALL_CASES, CacheManager.instance?.caseModel)
//            intent.removeExtra(AppConstants.KEY_CASES_ARRAY)
        }

        setAdapter(CacheManager.instance?.caseModel)

    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.clear()
        super.onSaveInstanceState(outState, outPersistentState)

    }

    private fun setAdapter(caseModel: CaseModel?) {
        if (null != caseModel && caseModel.cases?.size!! > 0) {
            emptyListView.visibility = View.GONE
            val bankRecyclerView = findViewById<RecyclerView>(R.id.bankUsersRecyclerView)
            bankRecyclerView.layoutManager = LinearLayoutManager(this)
            val bankUsersAdapter = CasesAdapter(this, caseModel.cases as ArrayList<Case>, sharedViewModel)
            bankRecyclerView.adapter = bankUsersAdapter
        } else emptyListView.visibility = View.VISIBLE

    }

    override fun onSuccess(message: Response<CaseModel>) {
        BaseMethods.finishprogress()
        val tinyDB = TinyDB(this)
        tinyDB.clear()
        sync.visibility = View.GONE
        Toast.makeText(this, message.message(), Toast.LENGTH_SHORT).show()
        viewModel.getCases()

    }

    override fun onFailure(message: String?) {
        BaseMethods.finishprogress()
        sync.visibility = View.GONE
        Toast.makeText(this, "Error Occur", Toast.LENGTH_SHORT).show()

    }

    override fun onCaseSuccess(caseModel: Response<CaseModel>) {
        setAdapter(caseModel.body())
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

    fun showLogoutAlert() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Alert")
            setMessage(getString(R.string.want_to_logout))
            setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
            show()
        }


    }
    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
        logout()
    }
    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
    }


    fun showHomeAlert() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Alert")
            setMessage(getString(R.string.goto_homescreen))
            setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener(function = positiveHomeButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
            show()
        }


    }
    private val positiveHomeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
        finish()
        val intent = Intent(this, BanksListActivity::class.java)
        startActivity(intent)
    }


    private fun logout() {
        BaseMethods.progressdialog(this)

        val userService = NetworkClient.createService(UserService::class.java)
        val call = userService.sign_out(SharedPreferences.getSharedPreferences("access-token", BaseApplication.getContext()),
                SharedPreferences.getSharedPreferences("client", BaseApplication.getContext()),
                SharedPreferences.getSharedPreferences("uid", BaseApplication.getContext()))
        call.enqueue(object : Callback<CaseModel> {
            override fun onResponse(call: Call<CaseModel>, response: Response<CaseModel>) {
                BaseMethods.finishprogress()
                if (response.isSuccessful) {
                    SharedPreferences.saveSharedPreference(AppConstants.KEY_SHARED_PREFERENCE_LOGGED, AppConstants.NO, BaseApplication.getContext())
                    finish()
                    startActivity(Intent(this@CasesActivity, LoginActivity::class.java))
                } else
                    Toast.makeText(this@CasesActivity, "Error! Please try again!", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<CaseModel>, t: Throwable) {
                BaseMethods.finishprogress()
                Toast.makeText(this@CasesActivity, "Error! Please try again!", Toast.LENGTH_SHORT).show()
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
        savedInstanceState?.clear()
        super.onRestoreInstanceState(savedInstanceState)
    }

}