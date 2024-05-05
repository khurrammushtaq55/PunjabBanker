package com.mmushtaq.bank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmushtaq.bank.model.Case

class SubmissionViewModel : ViewModel() {
    private var sectionLiveData: MutableLiveData<Case> = MutableLiveData<Case>()
    private var section: Case = Case()

    fun getCaseModel(): LiveData<Case> {
        sectionLiveData.value = section
        return sectionLiveData
    }

    fun setSectionCase(section: Case) {
        this.section = section
        sectionLiveData.value = section
    }

}