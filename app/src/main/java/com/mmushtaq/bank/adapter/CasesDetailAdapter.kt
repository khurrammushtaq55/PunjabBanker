package com.mmushtaq.bank.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmushtaq.bank.R
import com.mmushtaq.bank.model.Header

class CasesDetailAdapter(context: Context, headers: MutableList<Header>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val headers: List<Header>?
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_case_details, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        val header = headers!![position]
        viewHolder.label.text = header.key
        viewHolder.field.text = header.value

    }

    override fun getItemCount(): Int {
        return headers?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var label: TextView = itemView.findViewById(R.id.label)
        var field: TextView = itemView.findViewById(R.id.field)

    }

    init {
        this.headers = headers
        this.context = context
    }
}