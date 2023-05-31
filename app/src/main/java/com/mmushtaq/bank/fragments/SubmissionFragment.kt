package com.mmushtaq.bank.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mmushtaq.bank.R
import com.mmushtaq.bank.activities.BanksListActivity
import com.mmushtaq.bank.activities.CasesActivity
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.remote.AppConstants
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.BaseMethods.Companion.haveNetworkConnection
import com.mmushtaq.bank.utils.CacheManager.case
import com.mmushtaq.bank.utils.TinyDB
import com.mmushtaq.bank.viewmodel.SectionsViewModel
import com.mmushtaq.bank.viewmodel.SharedSubmittedDataViewModel
import kotlinx.android.synthetic.main.submission_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class SubmissionFragment : BaseFragment(),
        SectionsViewModel.ServerResponse {

    private lateinit var viewModel: SectionsViewModel
    private lateinit var sharedViewModel: SharedSubmittedDataViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.submission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SectionsViewModel::class.java)
        viewModel.serverResponse = this
        sharedViewModel = ViewModelProvider(this).get(SharedSubmittedDataViewModel::class.java)
        sharedViewModel.setSectionCase(case)
        val list = sharedViewModel.getCaseModel().value!!
        BaseMethods.hideKeyboard(requireActivity())
        if (haveNetworkConnection(requireActivity())) {
            txtViewSubmit.setText(getString(R.string.submit_form_msg))
            btnSumbit.setOnClickListener {
                BaseMethods.progressdialog(requireActivity())

                CoroutineScope(Dispatchers.IO).launch {
                    addDateTime(arrayListOf(list))
                    viewModel.saveCase(arrayListOf(list))
                }
            }
        } else {
            txtViewSubmit.setText(getString(R.string.no_network_msg))
            btnSumbit.setText("Save Locally")
            btnSumbit.setOnClickListener {
                BaseMethods.progressdialog(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    addDateTime(arrayListOf(list))
                    addCasesInTinyDb()

                }
            }

        }


    }


    private fun addDateTime(cases: ArrayList<Case>) {
        if (!cases.isNullOrEmpty()) {
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
        if (null != tinyDb.getObject(AppConstants.KEY_CASES)) {
            if (tinyDb.getObject(AppConstants.KEY_CASES).isNullOrEmpty()) {
                tinyDb.putObject(AppConstants.KEY_CASES, arrayListOf(case))
            } else {
                val newList = tinyDb.getObject(AppConstants.KEY_CASES)
                newList!!.add(case)
                tinyDb.putObject(AppConstants.KEY_CASES, newList)
            }
        }
        if (null != tinyDb.getObject(AppConstants.KEY_ALL_CASES)) {
            val allCaseArray: CaseModel? = tinyDb.getCaseObject(AppConstants.KEY_ALL_CASES)!!

            for (i in 0..allCaseArray?.cases?.size!!) {
                if (allCaseArray.cases[i].id == case.id) {
                    allCaseArray.cases.removeAt(i)
                    break
                }
            }
            tinyDb.putCaseObject(AppConstants.KEY_ALL_CASES, allCaseArray)
        }
        requireActivity().finish()
        val intent = Intent(requireContext(), CasesActivity::class.java)
        startActivity(intent)

    }

    override fun onSuccess(message: Response<CaseModel>) {
        SharedPreferences.saveSharedPreference(AppConstants.KEY_SUBMITTED_COUNT, message.body()?.submitted_cases_count.toString(), activity)
        SharedPreferences.saveSharedPreference(AppConstants.KEY_PENDING_COUNT, message.body()?.pending_cases_count.toString(), activity)

        BaseMethods.finishprogress()
        Toast.makeText(requireContext(), "Case Submitted Successfully", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
        startActivity(Intent(requireContext(), BanksListActivity::class.java))
    }

    override fun onFailure(message: String?) {
        BaseMethods.finishprogress()
        Toast.makeText(requireContext(), "Error Occur! Please try again", Toast.LENGTH_SHORT).show()

    }

    override fun onCaseSuccess(message: Response<CaseModel>) {
        TODO("Not yet implemented")
    }


}