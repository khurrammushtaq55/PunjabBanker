package com.mmushtaq.bank.utils

import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel

object CacheManager {
    var caseResponseModel: CaseModel? = null
    lateinit var caseObj: Case
    var schemedCases: List<Case>?=null

}