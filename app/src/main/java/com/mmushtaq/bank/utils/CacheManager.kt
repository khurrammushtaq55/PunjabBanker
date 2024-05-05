package com.mmushtaq.bank.utils

import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel

object CacheManager {
    var caseModel: CaseModel? = null
    lateinit var case: Case
    var schemedCases: List<Case>?=null
    lateinit var caseList: ArrayList<Case>

}