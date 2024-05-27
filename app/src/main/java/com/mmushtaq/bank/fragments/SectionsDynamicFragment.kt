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
import com.mmushtaq.bank.interfaces.ServerResponse
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.model.Section
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager.caseObj
import com.mmushtaq.bank.utils.TinyDB
import com.mmushtaq.bank.viewmodel.SharedViewModel
import com.mmushtaq.bank.viewmodel.SubmissionViewModel
import kotlinx.android.synthetic.main.sections_dynamic_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class SectionsDynamicFragment(private var index: Int) : BaseFragment(),
    ServerResponse, SectionsAdapter.ValidateFieldListner {

    private lateinit var submittedDataViewModel: SubmissionViewModel
    private lateinit var sectionsAdapter: SectionsAdapter
    private lateinit var viewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sections_dynamic_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.serverResponse = this

        submittedDataViewModel = ViewModelProvider(this).get(SubmissionViewModel::class.java)
        submittedDataViewModel.setSectionCase(caseObj)

        BaseMethods.hideKeyboard(requireActivity())

        if (caseObj.sections == null) {
            Toast.makeText(activity, "There are no Sections against this Case", Toast.LENGTH_SHORT)
                .show()
            return
        }

        enableButton(validateFields(caseObj.sections[index]))

        setListener()

        sectionTitle.text = caseObj.sections[index].title
        /* if(index+1==case.sections.size)
             btnNext.text = getString(R.string.submit)*/

        rvSections.layoutManager = LinearLayoutManager(activity)
        sectionsAdapter = SectionsAdapter(requireActivity())
        sectionsAdapter.validateFieldListner = this
        rvSections.adapter = sectionsAdapter

        viewModel.getQuestions().observe(viewLifecycleOwner, Observer {
            sectionsAdapter.submitList(it, index)
        })
        viewModel.setQyestionsArray(submittedDataViewModel.getCaseModel().value!!.getSections()[index].questions)

    }

    private fun setListener() {

        btnNext.setOnClickListener {
            BaseMethods.hideKeyboard(requireActivity())

            val section = caseObj.sections[index]
            section.questions = sectionsAdapter.getFilledData()
            caseObj.sections[index] = section
            submittedDataViewModel.setSectionCase(caseObj)


            if ((index + 1) < caseObj.sections.size) {
                val ft = activity?.supportFragmentManager?.beginTransaction()
                if (caseObj.sections[index + 1].type == (getString(R.string.agent_info))) {
                    ft?.add(android.R.id.content, SectionsDynamicFragment(index + 2))
                        ?.addToBackStack(null)?.commit()
                } else {
                    ft?.add(android.R.id.content, SectionsDynamicFragment(index + 1))
                        ?.addToBackStack(null)?.commit()
                }

            } else {

                if (null != caseObj.documents_business_attributes && caseObj.documents_business_attributes.size > 0) {
                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    ft?.add(android.R.id.content, DocumentsBusinessFragment())
                        ?.addToBackStack("DocumentsResidenceFragment")?.commit()

                } else {

                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    ft?.add(android.R.id.content, SubmissionFragment())
                        ?.addToBackStack("SubmissionFragment")?.commit()

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
                BaseMethods.hideProgressDialog()
            }

            .setPositiveButton(getString(R.string.save)) { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    addDateTime(list);
                    if (message == (getString(R.string.submit_form_msg))) {
                        viewModel.saveCase(
                            list,
                            SharedPreferences.getSharedPreferences(
                                "access-token",
                                activity
                            ),
                            SharedPreferences.getSharedPreferences(
                                "client",
                                activity
                            ),
                            SharedPreferences.getSharedPreferences(
                                "uid",
                                activity
                            )
                        )
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
        if (null != tinyDb.getCasesArray(AppConstants.KEY_CASES)) {
            if (tinyDb.getCasesArray(AppConstants.KEY_CASES).isNullOrEmpty()) {
                tinyDb.putCasesArray(AppConstants.KEY_CASES, arrayListOf(caseObj))
            } else {
                val newList = tinyDb.getCasesArray(AppConstants.KEY_CASES)
                newList!!.add(caseObj)
                tinyDb.putCasesArray(AppConstants.KEY_CASES, newList)
            }
        }

        val allCaseArray: CaseModel? = tinyDb.getCasesResponseModel()

        for (i in 0..allCaseArray?.cases?.size!!) {
            if (allCaseArray.cases[i].id == caseObj.id) {
                allCaseArray.cases.removeAt(i)
                break
            }
        }
        tinyDb.putCasesResponseModel( allCaseArray)
        requireActivity().finish()
        val intent = Intent(activity, CasesActivity::class.java)
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
        Toast.makeText(activity, "Case Submitted Successfully", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
        startActivity(Intent(activity, BanksListActivity::class.java))
    }

    override fun onFailure(message: String?) {
        BaseMethods.hideProgressDialog()
        Toast.makeText(activity, "Error Occur! Please try again", Toast.LENGTH_SHORT).show()

    }

    override fun onCaseSuccess(message: Response<CaseModel>) {
    }

    private fun validateFields(section: Section): Boolean {
        var fieldStatus = true
        var finalStatus = true
        section.questions.forEach { it ->
            val isValid = null != it.selectedAnswer && it.selectedAnswer.length >= it.min_length
            if (it.isMandatory && isValid) {
                fieldStatus = true
            } else if (it.isMandatory && !isValid) {
                return false
            } else if (!it.isMandatory) {
                fieldStatus = true
            }
            if (!fieldStatus) {
                finalStatus = fieldStatus
            }

        }
        return finalStatus
    }

    override fun validateField(caseIndex: Int) {
        enableButton(validateFields(caseObj.sections[caseIndex]))
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