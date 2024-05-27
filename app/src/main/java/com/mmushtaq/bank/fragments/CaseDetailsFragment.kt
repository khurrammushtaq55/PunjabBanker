package com.mmushtaq.bank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmushtaq.bank.R
import com.mmushtaq.bank.adapter.CasesDetailAdapter
import com.mmushtaq.bank.model.Header
import com.mmushtaq.bank.utils.CacheManager
import com.mmushtaq.bank.viewmodel.SubmissionViewModel
import kotlinx.android.synthetic.main.tab_fragment_case_details.detailsNext


class CaseDetailsFragment() : BaseFragment() {
    private lateinit var submissionViewModel: SubmissionViewModel
    private var contentView: View? = null
    private val BACK_STACK_ROOT_TAG = "root_fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.tab_fragment_case_details, container, false)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        submissionViewModel =
            ViewModelProvider(this).get(SubmissionViewModel::class.java)
        val recyclerView = contentView?.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val casesDetailAdapter =
            handleArray(CacheManager.caseObj.header)?.let { CasesDetailAdapter(requireContext(), it) }
        recyclerView?.adapter = casesDetailAdapter

        detailsNext.setOnClickListener {

            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(android.R.id.content, DocumentsResidenceFragment())
                ?.addToBackStack(BACK_STACK_ROOT_TAG)?.commit()
        }
    }

    private fun handleArray(header: List<Header>?): MutableList<Header>? {
        val headerTempList: MutableList<Header> = mutableListOf()
        if (header != null) {
            for (item in header) {
                // body of loop
                if (item.value != null) headerTempList.add(item)

            }
        }
        return headerTempList
    }


}