package com.mmushtaq.bank.adapter

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
import com.mmushtaq.bank.utils.CacheManager
import com.mmushtaq.bank.viewmodel.SharedViewModel

class CasesAdapter(context: Context, cases: ArrayList<Case>, sharedViewModel: SharedViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cases: List<Case>?
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val context: Context
    private val sharedViewModel: SharedViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_banks_users, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        val cas = cases!![position]
        viewHolder.txtUserCnic.text = """${cas.getPresent_tehsil()} - ${cas.getPresent_district()}"""
        viewHolder.txtUserName.text = cas.getName()
        viewHolder.txtUserNo.text = cas.getPrimary_mobile()
        viewHolder.btnDetails.setOnClickListener {
            val fm: FragmentManager = (context as FragmentActivity).supportFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
//            sharedViewModel.setCase(cas)
//            sharedViewModel.setCaseArray(cases as ArrayList<Case>)
            //cas, cases as ArrayList<Case>
//            val args = Bundle()
//            args.putSerializable("key_model", sharedViewModel)
//            args.putSerializable("key_case", cas)
//            args.putSerializable("key_list", cases as ArrayList<Case>)
            CacheManager.instance?.case = cas
            CacheManager.instance?.caseList = cases as ArrayList<Case>
            ft.add(android.R.id.content, CaseDetailsFragment()).addToBackStack(null).commit()

        }
    }

    override fun getItemCount(): Int {
        return cases?.size ?: 0
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
        this.sharedViewModel = sharedViewModel
    }
}