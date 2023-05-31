package com.mmushtaq.bank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmushtaq.bank.model.Case

class SharedSubmittedDataViewModel : ViewModel() {
    private var sectionLiveData: MutableLiveData<Case> = MutableLiveData<Case>()
    private var section: Case = Case()

    private lateinit var case: Case
    private lateinit var caseList: ArrayList<Case>

    fun getCaseModel(): LiveData<Case> {
        sectionLiveData.value = section
        return sectionLiveData
    }

    fun setSectionCase(section: Case) {
        this.section = section
        sectionLiveData.value = section
    }

    fun setCase(case: Case) {
        this.case = case;
    }

    fun setCaseArray(caseList: ArrayList<Case>) {
        this.caseList = caseList
    }

    fun getCase(): Case {
        return case
    }

    fun getCaseArray(): ArrayList<Case> {
        return caseList
    }
}