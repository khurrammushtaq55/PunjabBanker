package com.mmushtaq.bank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmushtaq.bank.interfaces.ServerResponse
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel
import com.mmushtaq.bank.model.Question
import com.mmushtaq.bank.remote.BaseApplication
import com.mmushtaq.bank.remote.NetworkClient
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.service.CaseVerificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap

class SharedViewModel : ViewModel() {
    private var sectionLiveList: MutableLiveData<ArrayList<Question>> =
        MutableLiveData<ArrayList<Question>>()
    private var sectionList: ArrayList<Question> = ArrayList()
    lateinit var serverResponse: ServerResponse


    fun getQuestions(): LiveData<ArrayList<Question>> {
        sectionLiveList.value = sectionList
        return sectionLiveList
    }

    fun setQyestionsArray(ques: ArrayList<Question>) {
        sectionLiveList.value = ques
    }

    fun getCases(accessToken: String, client: String, uid: String) {
        val caseVerificationService =
            NetworkClient.createService(CaseVerificationService::class.java)
        val call = caseVerificationService.getCases(accessToken, client, uid, "*/*")
        call.enqueue(object : Callback<CaseModel> {
            override fun onResponse(call: Call<CaseModel>, response: Response<CaseModel>) {
                if (response.isSuccessful) serverResponse.onCaseSuccess(response)
            }

            override fun onFailure(call: Call<CaseModel>, t: Throwable) {

            }

        })
    }

    fun saveCase(cases: ArrayList<Case>?, accessToken: String, client: String, uid: String) {
        val caseVerificationService =
            NetworkClient.createService(CaseVerificationService::class.java)
        val call = caseVerificationService.updateCases(accessToken, client, uid, "*/*",
            cases?.let { convertFields(it) })
        call.enqueue(object : Callback<CaseModel> {
            override fun onResponse(call: Call<CaseModel>, response: Response<CaseModel>) {
                if (response.isSuccessful) serverResponse.onSuccess(response)
                else serverResponse.onFailure(null)
            }
            override fun onFailure(call: Call<CaseModel>, t: Throwable) {

                serverResponse.onFailure(null)
            }

        })
    }

    private fun convertFields(cases: ArrayList<Case>): ConcurrentHashMap<String, String> {
        val map = ConcurrentHashMap<String, String>()
        if (!cases.isNullOrEmpty()) {
            var i = 0
            var j = 0
            var x = 0
            cases.forEach { it ->
                map["cases[$i][id]"] = it.id.toString()
                map["cases[$i][status]"] = "in_progress"

                it.sections?.forEach { agent_ques ->
                    if (agent_ques.type == "agent_info") {
                        agent_ques.questions.forEach { question ->

                            if (question.keyboard_type.toString() == "current_date") {
                                map["cases[$i][case_question_answers_attributes][$j][question_id]"] =
                                    question.id.toString()
                                map["cases[$i][case_question_answers_attributes][$j][answer_description]"] =
                                    question.given_answer!!

                            }
                            if (question.keyboard_type.toString() == "current_time") {
                                map["cases[$i][case_question_answers_attributes][$j][question_id]"] =
                                    question.id.toString()
                                map["cases[$i][case_question_answers_attributes][$j][answer_description]"] =
                                    question.given_answer!!

                            }
                            j++
                        }


                    } else {
                        agent_ques.questions?.forEach { questions ->

                            map["cases[$i][case_question_answers_attributes][$j][question_id]"] =
                                questions.id.toString()
                            if (null != questions.answerId) {
                                map["cases[$i][case_question_answers_attributes][$j][answer_id]"] =
                                    questions.answerId
                            } else {
                                map["cases[$i][case_question_answers_attributes][$j][answer_id]"] =
                                    ""
                            }
                            if (null != questions.remarks) {
                                map["cases[$i][case_question_answers_attributes][$j][remarks]"] =
                                    questions.remarks.toString()
                            }
                            if (null != questions.selectedAnswer) {
                                map["cases[$i][case_question_answers_attributes][$j][answer_description]"] =
                                    questions.selectedAnswer.toString()
                            }

                            j++
                        }
                    }
                }
                it.documents_residence_attributes?.forEach { residence_docs ->
                    if (null != residence_docs.documentUrl) {
                        map["cases[$i][documents_attributes][$x][id]"] =
                            residence_docs.id.toString()

                        map["cases[$i][documents_attributes][$x][doc]"] =
                            residence_docs.documentUrl.toString()

                        if (null != residence_docs.longitude) {
                            map["cases[$i][documents_attributes][$x][longitude]"] =
                                residence_docs.longitude.toString()
                        }
                        if (null != residence_docs.latitude) {
                            map["cases[$i][documents_attributes][$x][latitude]"] =
                                residence_docs.latitude.toString()
                        }
                    }
                    x++

                }
                if (null != it.documents_business_attributes) {
                    it.documents_business_attributes?.forEach { business_docs ->
                        if (null != business_docs.documentUrl) {
                            map["cases[$i][documents_attributes][$x][id]"] =
                                business_docs.id.toString()

                            map["cases[$i][documents_attributes][$x][doc]"] =
                                business_docs.documentUrl.toString()

                            if (null != business_docs.longitude) {
                                map["cases[$i][documents_attributes][$x][longitude]"] =
                                    business_docs.longitude.toString()
                            }
                            if (null != business_docs.latitude) {
                                map["cases[$i][documents_attributes][$x][latitude]"] =
                                    business_docs.latitude.toString()
                            }
                        }
                        x++

                    }
                }
                i++
            }
        }
        return map
    }


}