package com.mmushtaq.bank.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.model.CaseModel

class TinyDB(context: Context) {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun getString(key: String?): String {
        if (preferences.contains(key)) {
            return preferences.getString(key, "")!!
        }
        return ""
    }

    fun getCasesArray(key: String?): ArrayList<Case> {
        val json = getString(key)
        if (json.isNotEmpty()) {
            val list: ArrayList<Case>
            val gson = GsonBuilder().create()
            list = gson.fromJson(json, Array<Case>::class.java).toMutableList() as ArrayList<Case>
            return list
        }
        return ArrayList<Case>()
    }

    fun getCasesResponseModel(): CaseModel? {
        val json = getString(AppConstants.KEY_ALL_CASES)
        if (json.isNotEmpty()) {
            val case: CaseModel
            val gson = GsonBuilder().create()
            case = gson.fromJson(json, CaseModel::class.java) as CaseModel
            return case
        }
        return null
    }

    private fun putString(key: String?, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    fun putCasesArray(key: String?, obj: ArrayList<Case>?) {
        val gson = Gson()
        if (null != obj) {
            putString(key, gson.toJson(obj))
        }
    }

    fun putCasesResponseModel(obj: CaseModel?) {
        val gson = Gson()
        if (null != obj) {
            putString(AppConstants.KEY_ALL_CASES, gson.toJson(obj))
        }
    }

    fun remove(key: String?) {
        preferences.edit().remove(key).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }


}