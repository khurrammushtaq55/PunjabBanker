package com.mmushtaq.bank.utils

import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel

object CacheManager {
    private var cacheManager: CacheManager? = null

    //Now we are providing gloabal point of access.
    val instance: CacheManager?
        get() {
            if (cacheManager == null) {
                cacheManager = CacheManager
            }
            return cacheManager
        }

    var caseModel: CaseModel? = null
    lateinit var case: Case
    lateinit var caseList: ArrayList<Case>

}