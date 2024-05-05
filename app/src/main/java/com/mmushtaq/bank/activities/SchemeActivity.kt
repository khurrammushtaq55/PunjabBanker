package com.mmushtaq.bank.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmushtaq.bank.R
import com.mmushtaq.bank.adapter.SchemesAdapter
import com.mmushtaq.bank.model.Case
import com.mmushtaq.bank.utils.CacheManager
import kotlinx.android.synthetic.main.activity_schema.schemasRecyclerView

class SchemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schema)


        val schemeMap = processCases(CacheManager.schemedCases)
        schemasRecyclerView.layoutManager = LinearLayoutManager(this)
        val schemeAdapter = SchemesAdapter(
            this, schemeMap
        )
        schemasRecyclerView.adapter = schemeAdapter

    }

    private fun processCases(cases: List<Case>?): Map<String, MutableList<Case>>? {
        val bankSchemeMap: MutableMap<String, MutableList<Case>> = HashMap()
        if (cases != null) {
            for (caseItem in cases) {
                val scheme = caseItem.getScheme()

                // Check if the bank name is already in the map
                if (bankSchemeMap.containsKey(scheme)) {
                    // If bank name exists, add the scheme to its list
                    bankSchemeMap[scheme]!!.add(caseItem)
                } else {
                    // If bank name does not exist, create a new list for it and add the scheme
                    val schemeList: MutableList<Case> = ArrayList()
                    schemeList.add(caseItem)
                    bankSchemeMap[scheme] = schemeList
                }
            }
        }
        return bankSchemeMap
    }

    fun goToCaseList(caseList: MutableList<Case>?) {

        if (caseList.isNullOrEmpty()) {
            return
        }
        CacheManager.schemedCases = caseList

        val intent = Intent(this@SchemeActivity, CasesActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}