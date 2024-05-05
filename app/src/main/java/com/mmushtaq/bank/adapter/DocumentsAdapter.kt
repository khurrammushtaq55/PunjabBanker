package com.mmushtaq.bank.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mmushtaq.bank.R
import com.mmushtaq.bank.model.Documents
import com.mmushtaq.bank.utils.AppConstants
import kotlin.reflect.KFunction1

class DocumentsAdapter(
    context: Context,
    cases: List<Documents>?,
    val itemClick: KFunction1<String, Unit>,
    private val clickListener: ((position: Int, view: Button, cancel: ImageView, container: ConstraintLayout) -> Unit)? = null,
    private val removeItem: ((position: Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val docsList: ArrayList<Documents>?
    private val mLayoutInflater: LayoutInflater
    private val context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.document_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        val document = docsList!![position]

        setData(document, viewHolder)

        setCrossVisibilty(document, viewHolder)

        setListener(viewHolder, document, position)

        if (document.coordinates_required!! && document.isManualLatLng!! &&
            viewHolder.upload.text == context.getString(R.string.img_uploaded)
        ) {
            viewHolder.latLngContainer.visibility = View.VISIBLE
            viewHolder.latitude.setText(document.latitude!!)
            viewHolder.longitude.setText(document.longitude!!)

        } else {
            viewHolder.latLngContainer.visibility = View.GONE
        }
    }

    private fun setListener(viewHolder: MyViewHolder, cas: Documents, position: Int) {
        viewHolder.upload.setOnClickListener {
            clickListener?.let { it1 ->
                it1(

                    position, viewHolder.upload, viewHolder.remDoc, viewHolder.latLngContainer

                )
            }
            viewHolder.latitude.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) {
                        cas.latitude = s.toString()
                        docsList?.set(position, cas)
                        itemClick(s.toString())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun afterTextChanged(s: Editable) {

                }
            })
            viewHolder.longitude.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) {
                        cas.longitude = s.toString()
                        docsList?.set(position, cas)
                        itemClick(s.toString())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
        viewHolder.remDoc.setOnClickListener {
            removeItem?.let { it1 -> it1(position) }
            viewHolder.remDoc.visibility = View.GONE
            viewHolder.latLngContainer.visibility = View.GONE
            viewHolder.upload.text = context.getString(R.string.upload)
        }
    }

    private fun setCrossVisibilty(cas: Documents, viewHolder: MyViewHolder) {
        if (cas.isAdded!!) {
            viewHolder.remDoc.visibility = View.VISIBLE
            viewHolder.upload.text = context.getString(R.string.img_uploaded)
        } else {
            viewHolder.remDoc.visibility = View.GONE
            viewHolder.upload.text = context.getString(R.string.upload)
        }

    }

    private fun setData(cas: Documents, viewHolder: MyViewHolder) {

        if (!cas.description.isNullOrEmpty()) {
            if (cas.nature == AppConstants.KEY_MANDATORY) {
                viewHolder.docName.text = """${cas.description}*"""
            } else {
                viewHolder.docName.text = cas.description
            }
        }

    }

    override fun getItemCount(): Int {
        return docsList?.size ?: 0
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getFilledData(): ArrayList<Documents>? {
        return docsList
    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var upload: Button = itemView.findViewById(R.id.docAttach)
        var docName: TextView = itemView.findViewById(R.id.docName)
        var remDoc: ImageView = itemView.findViewById(R.id.remDoc)
        var latitude: EditText = itemView.findViewById(R.id.ed_lat)
        var longitude: EditText = itemView.findViewById(R.id.ed_lng)
        var latLngContainer: ConstraintLayout = itemView.findViewById(R.id.latlngcontainer)


    }


    init {
        mLayoutInflater = LayoutInflater.from(context)
        this.docsList = cases as ArrayList<Documents>?
        this.context = context
    }


}