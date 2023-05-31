package com.mmushtaq.bank.viewmodel

import androidx.lifecycle.ViewModel
import com.mmushtaq.bank.model.Case
import java.io.Serializable

class SharedViewModel : ViewModel(), Serializable {

    private lateinit var case: Case
    private lateinit var caseList: ArrayList<Case>


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