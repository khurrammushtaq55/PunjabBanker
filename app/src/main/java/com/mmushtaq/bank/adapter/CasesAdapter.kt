package com.mmushtaq.bank.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.mmushtaq.bank.R
import com.mmushtaq.bank.fragments.CaseDetailsFragment
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager
import java.util.Locale


class CasesAdapter(context: Activity, cases: ArrayList<Case>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cases: List<Case>?
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val context: Activity
    private var tempCases: ArrayList<Case>?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_cases, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        if (!tempCases.isNullOrEmpty()) {
            val tempCase = tempCases!![position]
            viewHolder.txtUserCnic.text =
                """${tempCase.present_tehsil} - ${tempCase.present_district}"""
            viewHolder.txtUserName.text = tempCase.name
            viewHolder.txtUserNo.text = tempCase.primary_mobile
            viewHolder.btnDetails.setOnClickListener {
                BaseMethods.hideKeyboard(context)
                val fm: FragmentManager = (context as FragmentActivity).supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()

                val originalCase = getOriginalCase(tempCase)

                if (originalCase != null) {
                    CacheManager.caseObj = originalCase
                    ft.add(android.R.id.content, CaseDetailsFragment()).addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun getOriginalCase(tempCase: Case): Case? {

        for (case in CacheManager.caseResponseModel?.getCases()!!) {
            if (tempCase.id == case.id) {
                return case
            }
        }
        return null
    }

    // filter name in Search Bar
    fun filter(searchText: String) {
        if (searchText.isEmpty()) {
            if (cases != null) {
                tempCases = cases.map { it.copy() } as ArrayList<Case>?
            }
        } else {

            tempCases?.clear()
            if (cases != null) {
                for (case in cases) {
                    if (case.name.toLowerCase(Locale.ROOT).contains(searchText)
                        || case.present_tehsil.toLowerCase(Locale.ROOT).contains(searchText)
                        || case.present_district.toLowerCase(Locale.ROOT).contains(searchText)
                        || case.primary_mobile.toLowerCase(Locale.ROOT).contains(searchText)
                    ) {
                        tempCases?.add(case)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return tempCases?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnDetails: Button
        var txtUserCnic: TextView
        var txtUserName: TextView
        var txtUserNo: TextView

        init {
            btnDetails = itemView.findViewById(R.id.btnDetails)
            txtUserCnic = itemView.findViewById(R.id.txtUserCnic)
            txtUserName = itemView.findViewById(R.id.txtUserName)
            txtUserNo = itemView.findViewById(R.id.txtUserNo)
        }
    }

    init {
        this.cases = cases
        this.context = context
        this.tempCases = cases.map { it.copy() } as ArrayList<Case>?
    }
}