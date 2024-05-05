package com.mmushtaq.bank.interfaces

import com.mmushtaq.bank.model.CaseModel
import retrofit2.Response

interface ServerResponse
{
    fun onSuccess(message: Response<CaseModel>)
    fun onFailure(message: String?)
    fun onCaseSuccess(message: Response<CaseModel>)
}