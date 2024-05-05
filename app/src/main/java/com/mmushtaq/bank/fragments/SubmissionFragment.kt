package com.mmushtaq.bank.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mmushtaq.bank.R
import com.mmushtaq.bank.activities.BanksListActivity
import com.mmushtaq.bank.interfaces.ServerResponse
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager.case
import com.mmushtaq.bank.utils.TinyDB
import com.mmushtaq.bank.viewmodel.SharedViewModel
import com.mmushtaq.bank.viewmodel.SubmissionViewModel
import kotlinx.android.synthetic.main.submission_fragment.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class SubmissionFragment : BaseFragment(),
    ServerResponse {

    private lateinit var viewModel: SharedViewModel
    private lateinit var submittedDataViewModel: SubmissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.submission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.serverResponse = this
        submittedDataViewModel = ViewModelProvider(this).get(SubmissionViewModel::class.java)
        submittedDataViewModel.setSectionCase(case)
        val list = submittedDataViewModel.getCaseModel().value!!
        BaseMethods.hideKeyboard(requireActivity())
        if (BaseMethods.haveNetworkConnection(requireActivity())) {
            txtViewSubmit.text = getString(R.string.submit_form_msg)
            btnSumbit.setOnClickListener {
                BaseMethods.showProgressDialog(requireActivity())

                CoroutineScope(Dispatchers.IO).launch {
                    addDateTime(arrayListOf(list))
                    viewModel.saveCase(
                        arrayListOf(list),
                        SharedPreferences.getSharedPreferences(
                            "access-token",
                            activity
                        ),
                        SharedPreferences.getSharedPreferences("client", activity),
                        SharedPreferences.getSharedPreferences("uid", activity)
                    )
                }
            }
        } else {
            txtViewSubmit.text = getString(R.string.no_network_msg)
            btnSumbit.text = "Save Locally"
            btnSumbit.setOnClickListener {


                val handler = CoroutineExceptionHandler { _, exception ->
                    BaseMethods.hideProgressDialog()
                    Toast.makeText(activity,"Exception occurred while saving data",Toast.LENGTH_SHORT).show()
                    Log.e("CoroutineExceptionHandler", "Caught $exception")
                }
                CoroutineScope(Dispatchers.IO + handler).launch {
//                    BaseMethods.showProgressDialog(requireActivity())
                    addDateTime(arrayListOf(list))
                    addCasesInTinyDb()

                }
            }

        }


    }


    private fun addDateTime(cases: ArrayList<Case>) {
        if (cases.isNotEmpty()) {
            cases.forEach { case ->
                case.sections.forEach { sections ->
                    if (sections.type == "agent_info") {

                        sections.questions.forEach { question ->

                            if (question.keyboard_type.toString() == "current_date") {
                                question.given_answer = BaseMethods.getCurrentDate()

                            }
                            if (question.keyboard_type.toString() == "current_time") {
                                question.given_answer = BaseMethods.getCurrentTime()

                            }
                        }

                    }
                }
            }
        }
    }

    private fun addCasesInTinyDb() {
        val tinyDb = TinyDB(requireActivity())
        if (null != tinyDb.getCasesArray(AppConstants.KEY_CASES)) {
            if (tinyDb.getCasesArray(AppConstants.KEY_CASES).isEmpty()) {
                tinyDb.putCasesArray(AppConstants.KEY_CASES, arrayListOf(case))
            } else {
                val newList = tinyDb.getCasesArray(AppConstants.KEY_CASES)
                newList?.add(case)
                tinyDb.putCasesArray(AppConstants.KEY_CASES, newList)
            }
        }
        if (null != tinyDb.getCaseModel(AppConstants.KEY_ALL_CASES)) {
            val allCaseArray: CaseModel? = tinyDb.getCaseModel(AppConstants.KEY_ALL_CASES)!!

            for (i in 0..allCaseArray?.cases?.size!!) {
                if (allCaseArray.cases[i].id == case.id) {
                    allCaseArray.cases.removeAt(i)
                    break
                }
            }
            tinyDb.putCaseModel(AppConstants.KEY_ALL_CASES, allCaseArray)
        }
        requireActivity().finishAffinity()
        val intent = Intent(requireContext(), BanksListActivity::class.java)
        startActivity(intent)


    }

    override fun onSuccess(message: Response<CaseModel>) {
        SharedPreferences.saveSharedPreference(
            AppConstants.KEY_SUBMITTED_COUNT,
            message.body()?.submitted_cases_count.toString(),
            activity
        )
        SharedPreferences.saveSharedPreference(
            AppConstants.KEY_PENDING_COUNT,
            message.body()?.pending_cases_count.toString(),
            activity
        )

        BaseMethods.hideProgressDialog()
        Toast.makeText(requireContext(), "Case Submitted Successfully", Toast.LENGTH_SHORT).show()
        requireActivity().finishAffinity()
        startActivity(Intent(requireContext(), BanksListActivity::class.java))
    }

    override fun onFailure(message: String?) {
        BaseMethods.hideProgressDialog()
        Toast.makeText(requireContext(), "Error Occur! Please try again", Toast.LENGTH_SHORT).show()

    }

    override fun onCaseSuccess(message: Response<CaseModel>) {
        TODO("Not yet implemented")
    }


}