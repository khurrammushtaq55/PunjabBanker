package com.mmushtaq.bank.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmushtaq.bank.R
import com.mmushtaq.bank.model.Question
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods
import com.tiper.MaterialSpinner
import java.util.*


class SectionsAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var questions: ArrayList<Question>
    private var fieldIndex = 0
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var validateFieldListner: ValidateFieldListner
    private lateinit var viewHolder: MyViewHolder


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.partner_info_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewHolder = holder as MyViewHolder

        val question = questions[position]


        setDropDownView(question, viewHolder, position)

        setListener(viewHolder)

        setRemarksViewListener(question, viewHolder, position)
        setEdittextViewListener(question, viewHolder, position)
        setData(question, viewHolder)


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener(viewHolder: MyViewHolder) {
//         Set up touch listener for non-text box views to hide keyboard.
//         Set up touch listener for non-text box views to hide keyboard.
        if (viewHolder.itemView !is EditText) {
            viewHolder.itemView.setOnTouchListener { v, event ->
                BaseMethods.hideKeyboard(context as Activity)
                false
            }
        }

    }

    private fun setRemarksViewListener(
        question: Question,
        viewHolder: MyViewHolder,
        position: Int
    ) {
        viewHolder.remarks.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    if (!question.answers.isNullOrEmpty()) {
                        question.remarks = s.toString()
                    }
                    questions[position] = question

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    private fun setEdittextViewListener(
        question: Question,
        viewHolder: MyViewHolder,
        position: Int
    ) {

        viewHolder.edtTextSection.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                BaseMethods.hideKeyboard(context as Activity)
            } else {
                if (!viewHolder.edtTextSection.requestFocus(View.FOCUS_RIGHT)) {
                    BaseMethods.hideKeyboard(context as Activity)
                }
            }
            false
        }
        viewHolder.edtTextSection.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                question.selectedAnswer = removeDashes(s.toString())
                validateFieldListner.validateField(caseIndex = fieldIndex)
                questions[position] = question
            }

        })

    }

    private fun removeDashes(text: String): String? {

        if (text.isNotEmpty()) {
            return text.replace("-", "")
        }
        return text
    }

    private fun setDropDownView(question: Question, viewHolder: MyViewHolder, position: Int) {
        if (!question.answers.isNullOrEmpty() && question.question_type == (AppConstants.QuestionType.TYPE_DROPDOWN)) {

            viewHolder.edtTextSection.visibility = View.GONE
            viewHolder.selectrSection.visibility = View.VISIBLE

            val newAnswer = ArrayList<String>()
            question.answers.forEach { it ->
                newAnswer.add(it.description)
                /*if(it.isRemarks_required)
                {
                    viewHolder.remarks.visibility = View.VISIBLE
                }else
                {*/
                viewHolder.remarks.visibility = View.GONE
//                }
            }
            question.question_type
            val arrayAdapter =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, newAnswer)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewHolder.selectrSection.adapter = arrayAdapter
            val listener by lazy {
                object : MaterialSpinner.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: MaterialSpinner,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        BaseMethods.hideKeyboard(context as Activity)
                        question.selectedAnswer = newAnswer[pos]
                        question.answerId = question.answers[pos].id.toString()
                        question.isFieldFilled = true
                        questions[position] = question
                        validateFieldListner.validateField(caseIndex = fieldIndex)
                        if (question.answers[pos].isRemarks_required) {
                            viewHolder.remarks.visibility = View.VISIBLE
                        } else {
                            viewHolder.remarks.visibility = View.GONE
                        }

                    }

                    override fun onNothingSelected(parent: MaterialSpinner) {
                        BaseMethods.hideKeyboard(context as Activity)
                    }
                }
            }
            viewHolder.selectrSection.onItemSelectedListener = listener
        } else {
            viewHolder.remarks.visibility = View.GONE
            viewHolder.edtTextSection.visibility = View.VISIBLE
            viewHolder.selectrSection.visibility = View.GONE
        }

    }

    private fun setData(question: Question, viewHolder: MyViewHolder) {
        viewHolder.partnerQue.text = question.description
        if (!question.given_answer.isNullOrEmpty())
            viewHolder.edtTextSection.setText(question.given_answer!!)
        if (null != question.keyboard_type) {
            setKeyBoardType(question.keyboard_type, viewHolder, question)
        }


        if (!question.selectedAnswer.isNullOrEmpty() && question.question_type == (AppConstants.QuestionType.TYPE_STRING))
            viewHolder.edtTextSection.setText(question.selectedAnswer)
        else if (question.question_type == (AppConstants.QuestionType.TYPE_STRING))
            viewHolder.edtTextSection.setText(AppConstants.KEY_EMPTY)


        if (!question.answers.isNullOrEmpty())
            for (i in 0 until question.answers.size) {
                if (question.answers[i].description == question.selectedAnswer) {
                    viewHolder.selectrSection.selection = (i)
                    break
                }
            }

    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var partnerQue: TextView = itemView.findViewById(R.id.partnerQuestion)
        var edtTextSection: EditText = itemView.findViewById(R.id.partnerAnswer)
        var remarks: EditText = itemView.findViewById(R.id.remarks)
        var selectrSection: MaterialSpinner = itemView.findViewById(R.id.partnerAnswers)

    }

    fun submitList(questions: ArrayList<Question>, caseIndex: Int) {
        this.questions = questions
        this.fieldIndex = caseIndex
        notifyDataSetChanged()
    }

    fun getFilledData(): ArrayList<Question>? {
        return questions
    }

    private fun setKeyBoardType(type: String, viewholder: MyViewHolder, question: Question) {
        if (question.max_length > 0)
            viewholder.edtTextSection.filters =
                arrayOf(InputFilter.LengthFilter(question.max_length))

        when (type) {

            AppConstants.KeyboardType.TYPE_STRING -> {
                viewholder.edtTextSection.inputType = InputType.TYPE_CLASS_TEXT
            }

            AppConstants.KeyboardType.TYPE_MOBILE -> {
                viewholder.edtTextSection.inputType = InputType.TYPE_CLASS_NUMBER
                /* viewholder.edtTextSection.addTextChangedListener(PhoneNumberTextWatcher(viewholder.edtTextSection));
                 viewholder.edtTextSection.hint = "0000-0000-000"
                 viewholder.edtTextSection.filters = arrayOf(InputFilter.LengthFilter(question.max_length+2))*/

            }

            AppConstants.KeyboardType.TYPE_CNIC -> {
                viewholder.edtTextSection.inputType = InputType.TYPE_CLASS_NUMBER
                /* viewholder.edtTextSection.addTextChangedListener(CnicTextWatcher(viewholder.edtTextSection));
                 viewholder.edtTextSection.hint = "00000-0000000-0"
                 viewholder.edtTextSection.filters = arrayOf(InputFilter.LengthFilter(question.max_length+2))*/
            }

            AppConstants.KeyboardType.TYPE_ISSUE_DATE -> {

                openDatePcker(viewholder.edtTextSection)


            }

        }

    }

    private fun openDatePcker(partnerAnsField: EditText) {
        partnerAnsField.isFocusable = false
        partnerAnsField.isFocusableInTouchMode = false
        partnerAnsField.isClickable = true
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        partnerAnsField.setOnClickListener {

            val dpd = DatePickerDialog(context, { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                val mont = monthOfYear + 1
                partnerAnsField.setText("$dayOfMonth/$mont/$year")
            }, year, month, day)
            dpd.show()

        }

    }

    interface ValidateFieldListner {
        fun validateField(caseIndex: Int)
    }
}