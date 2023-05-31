package com.mmushtaq.bank.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmushtaq.bank.R
import com.mmushtaq.bank.activities.BanksListActivity
import com.mmushtaq.bank.activities.CasesActivity
import com.mmushtaq.bank.adapter.SectionsAdapter
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.model.Section
import com.mmushtaq.bank.remote.AppConstants
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager.case
import com.mmushtaq.bank.utils.TinyDB
import com.mmushtaq.bank.viewmodel.SectionsViewModel
import com.mmushtaq.bank.viewmodel.SharedSubmittedDataViewModel
import kotlinx.android.synthetic.main.sections_dynamic_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class SectionsDynamicFragment(var index: Int) : BaseFragment(),
        SectionsViewModel.ServerResponse, SectionsAdapter.ValidateFieldListner {

    private lateinit var sharedViewModel: SharedSubmittedDataViewModel
    private lateinit var sectionsAdapter: SectionsAdapter
    private lateinit var viewModel: SectionsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sections_dynamic_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SectionsViewModel::class.java)
        viewModel.serverResponse = this

        sharedViewModel = ViewModelProvider(this).get(SharedSubmittedDataViewModel::class.java)
        sharedViewModel.setSectionCase(case)

        BaseMethods.hideKeyboard(requireActivity())


        enableButton(validateFields(case.sections[index]))

        setListener()

        sectionTitle.text = case.sections[index].title
        /* if(index+1==case.sections.size)
             btnNext.text = getString(R.string.submit)*/

        rvSections.layoutManager = LinearLayoutManager(activity)
        sectionsAdapter = SectionsAdapter(requireActivity())
        sectionsAdapter.validateFieldListner = this
        rvSections.adapter = sectionsAdapter

        viewModel.getQuestions().observe(viewLifecycleOwner, Observer {
            sectionsAdapter.submitList(it, index)
        })
        viewModel.setQyestionsArray(sharedViewModel.getCaseModel().value!!.getSections()[index].questions)

    }

    private fun setListener() {

        btnNext.setOnClickListener {
            BaseMethods.hideKeyboard(requireActivity())

            val section = case.sections[index]
            section.questions = sectionsAdapter.getFilledData()
            case.sections[index] = section
            sharedViewModel.setSectionCase(case)


            if ((index + 1) < case.sections.size) {
                val ft = activity?.supportFragmentManager?.beginTransaction()
                if (case.sections[index + 1].type == (getString(R.string.agent_info))) {
                    ft?.add(android.R.id.content, SectionsDynamicFragment(index + 2))?.addToBackStack(null)?.commit()
                } else {
                    ft?.add(android.R.id.content, SectionsDynamicFragment(index + 1))?.addToBackStack(null)?.commit()
                }

            } else {

                if (null != case.documents_business_attributes && case.documents_business_attributes.size > 0) {
                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    ft?.add(android.R.id.content, DocumentsBusinessFragment())?.addToBackStack("DocumentsResidenceFragment")?.commit()

                } else {

                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    ft?.add(android.R.id.content, SubmissionFragment())?.addToBackStack("SubmissionFragment")?.commit()

                    // goto final screen for submission
                    //take all this code to that screen todo
                    // and write cod in business docs too
                }
                /* if (haveNetworkConnection(requireActivity())) {
                     BaseMethods.progressdialog(requireActivity())
                     showAlertDialog(getString(R.string.submit_form_msg), arrayListOf(sharedViewModel.getCaseModel().value!!))
                 } else {
                     showAlertDialog(getString(R.string.no_network_msg), arrayListOf(sharedViewModel.getCaseModel().value!!))

                 }*/
            }
        }
/*
        isPartner.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    addPartnerRV.visibility = View.VISIBLE
                } else {
                    addPartnerRV.visibility = View.GONE
                }
            }
        })*/
    }

    private fun showAlertDialog(message: String, list: ArrayList<Case>) {
        AlertDialog.Builder(requireActivity())
//                .setTitle("Alert")
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    BaseMethods.finishprogress()
                }

                .setPositiveButton(getString(R.string.save)) { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {
                        addDateTime(list);
                        if (message == (getString(R.string.submit_form_msg))) {
                            viewModel.saveCase(list)
                        } else {
                            addCasesInTinyDb()

                        }


                    }

                }
                .show()
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

        val allCaseArray : CaseModel?  = tinyDb.getCaseObject(AppConstants.KEY_ALL_CASES)!!

        for (i in 0..allCaseArray?.cases?.size!!)
        {
            if(allCaseArray.cases[i].id==case.id) {
                allCaseArray.cases.removeAt(i)
                break
            }
        }
        tinyDb.putCaseObject(AppConstants.KEY_ALL_CASES, allCaseArray)
        requireActivity().finish()
        val intent = Intent(activity, CasesActivity::class.java)
        startActivity(intent)

    }

    override fun onSuccess(message: Response<CaseModel>) {
        SharedPreferences.saveSharedPreference(AppConstants.KEY_SUBMITTED_COUNT, message.body()?.submitted_cases_count.toString(), activity)
        SharedPreferences.saveSharedPreference(AppConstants.KEY_PENDING_COUNT, message.body()?.pending_cases_count.toString(), activity)

        BaseMethods.finishprogress()
        Toast.makeText(activity, "Case Submitted Successfully", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
        startActivity(Intent(activity, BanksListActivity::class.java))
    }

    override fun onFailure(message: String?) {
        BaseMethods.finishprogress()
        Toast.makeText(activity, "Error Occur! Please try again", Toast.LENGTH_SHORT).show()

    }

    override fun onCaseSuccess(message: Response<CaseModel>) {
    }

    private fun validateFields(section: Section): Boolean {
        var fieldStatus = true
        var finalStatus = true
        section.questions.forEach { it ->
            val isValid =null != it.selectedAnswer && it.selectedAnswer.length>=it.min_length
            if (it.isMandatory && isValid) {
                fieldStatus = true
            } else if (it.isMandatory && !isValid) {
                return false
            } else if(!it.isMandatory)
            {
                fieldStatus = true
            }
            if (!fieldStatus) {
                finalStatus = fieldStatus
            }

        }
        return finalStatus
    }

    override fun validateField(caseIndex: Int) {
        enableButton(validateFields(case.sections[caseIndex]))
    }


    private fun enableButton(boolean: Boolean) {
        if (boolean) {
            btnNext.isEnabled = true
            btnNext.alpha = 1f
        } else {
            btnNext.isEnabled = false
            btnNext.alpha = 0.5f

        }
    }

}