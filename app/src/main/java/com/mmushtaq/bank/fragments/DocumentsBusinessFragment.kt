package com.mmushtaq.bank.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.mmushtaq.bank.R
import com.mmushtaq.bank.adapter.DocumentsAdapter
import com.mmushtaq.bank.model.Documents
import com.mmushtaq.bank.remote.SharedPreferences
import com.mmushtaq.bank.utils.AppConstants
import com.mmushtaq.bank.utils.BaseMethods
import com.mmushtaq.bank.utils.CacheManager.caseObj
import com.mmushtaq.bank.utils.EasyLocationFetch
import com.mmushtaq.bank.utils.GeoLocationModel
import com.mmushtaq.bank.viewmodel.SubmissionViewModel
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.tab_fragment_documents.docNext
import kotlinx.android.synthetic.main.tab_fragment_documents.docRecycler
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException


class DocumentsBusinessFragment() : BaseFragment() {
    private lateinit var submissionViewModel: SubmissionViewModel
    private var contentView: View? = null
    private var documentsAdapter: DocumentsAdapter? = null
    private var btn: Button? = null
    private var containerLatLng: ConstraintLayout? = null
    private var removeBtn: ImageView? = null
    private var currentPosition = 0


    private var actualImage: File? = null
    private var compressedImage: File? = null
    private var ar: ByteArray? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.tab_fragment_documents, container, false)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        submissionViewModel = ViewModelProvider(this).get(SubmissionViewModel::class.java)

        enableButton(validateFields(caseObj.documents_business_attributes))
        setRecyclerView()
        setListener()

        savedInstanceState?.clear()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.clear()
        super.onSaveInstanceState(outState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.clear()
    }

    private fun setListener() {
        docNext.setOnClickListener {
            caseObj.documents_business_attributes = documentsAdapter!!.getFilledData()
            submissionViewModel.setSectionCase(caseObj)

            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(android.R.id.content, SubmissionFragment())
                ?.addToBackStack("SubmissionFragment")?.commit()
        }

    }

    private fun setRecyclerView() {
        docRecycler.layoutManager
        docRecycler.layoutManager = LinearLayoutManager(activity)
        val documentAdapter = DocumentsAdapter(
            requireActivity(),
            caseObj.documents_business_attributes,
            ::itemClick,
            ::onUpload,
            ::removeAttachment
        )
        documentsAdapter = documentAdapter
        this.docRecycler.adapter = documentsAdapter

    }

    private fun onUpload(
        position: Int,
        view: Button,
        removeButton: ImageView,
        container: ConstraintLayout
    ) {


        if (view.text != getString(R.string.img_uploaded)) {
            btn = view
            removeBtn = removeButton
            containerLatLng = container
            currentPosition = position
            if (AppConstants.YES == SharedPreferences.getSharedPreferences(
                    AppConstants.KEY_CAN_UPLOAD_PICTURE, activity
                )
            ) {
                createPickerDialog(position)
            } else {
                ImagePicker.with(this).cameraOnly().start()
                caseObj.documents_business_attributes[position].isManualLatLng = false
            }
        }
    }


    private fun removeAttachment(position: Int) {

        caseObj.documents_business_attributes[currentPosition].isAdded = false
        caseObj.documents_business_attributes[position].documentUrl = null
        enableButton(validateFields(caseObj.documents_business_attributes))

    }

    private fun itemClick(position: String) {

        enableButton(validateFields(caseObj.documents_business_attributes))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (ImagePicker.shouldHandle(requestCode, resultCode, data))

        // Get a list of picked images

        if (data == null) {
            showError("Failed to open picture!")
            return
        }

        BaseMethods.showProgressDialog(requireActivity())
        val image = ImagePicker.getFile(data)
        try {
            actualImage = ImagePicker.getFile(data)
//                actualImage = FileUtil.from(requireActivity(), image.uri)
            actualImage?.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    compressedImage = Compressor.compress(requireActivity(), imageFile)
                    setImage()
                }
            } ?: showError("Please choose an image!")

        } catch (e: IOException) {
            BaseMethods.hideProgressDialog()
            showError("Failed to read picture data!")
            e.printStackTrace()
        }
//            val ar = readBytes(requireActivity(), image.uri)


        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setImage() {
        compressedImage?.let {
            ar = FileUtils.readFileToByteArray(compressedImage!!)
            if (null != btn && removeBtn != null) {
                if (ar != null) btn?.text = getString(R.string.img_uploaded)
                removeBtn!!.visibility = View.VISIBLE
                if (caseObj.documents_business_attributes[currentPosition].coordinates_required!!) {
                    if (caseObj.documents_business_attributes[currentPosition].isManualLatLng!!) {

                        containerLatLng?.visibility = View.VISIBLE
                    } else {
                        registerLocation()
                        containerLatLng?.visibility = View.GONE
                    }
                }

                caseObj.documents_business_attributes[currentPosition].isAdded = true
                val encodedImage: String = Base64.encodeToString(ar, Base64.DEFAULT)
                caseObj.documents_business_attributes[currentPosition].documentUrl = encodedImage
                submissionViewModel.setSectionCase(caseObj)
                enableButton(validateFields(caseObj.documents_business_attributes))
                BaseMethods.hideProgressDialog()
            } else BaseMethods.hideProgressDialog()
        } ?: showError("Please retake image")


    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
    }


    private fun registerLocation() {

        try {


            val geoLocationModel: GeoLocationModel = EasyLocationFetch(activity).locationData
            if (geoLocationModel.lattitude == 0.0) {
                retake()


            } else {
                caseObj.documents_business_attributes[currentPosition].latitude =
                    geoLocationModel.lattitude.toString()
                caseObj.documents_business_attributes[currentPosition].longitude =
                    geoLocationModel.longitude.toString()
                showError("" + geoLocationModel.lattitude.toString() + " " + geoLocationModel.longitude.toString())
            }
        } catch (e: Exception) {
            retake()
        }

    }

    private fun retake() {
        showError("Please turn on Location from settings and then take picture")
        caseObj.documents_business_attributes[currentPosition].isAdded = false
        caseObj.documents_business_attributes[currentPosition].documentUrl = null
        submissionViewModel.setSectionCase(caseObj)
        enableButton(validateFields(caseObj.documents_business_attributes))
        btn!!.text = getString(R.string.upload)
        removeBtn!!.visibility = View.GONE


    }

    private fun enableButton(boolean: Boolean) {
        if (boolean) {
            docNext?.isEnabled = true
            docNext?.alpha = 1f
        } else {
            docNext?.isEnabled = false
            docNext?.alpha = 0.5f

        }
    }

    private fun validateFields(documents: List<Documents>): Boolean {
        //"id":10786,"document_url":null,"nature":"COMMON","description":"GUARANTEE DOCUMENT",
        // "document_type":"pdf","coordinates_required":false,"longitude":null,"latitude":null
        var fieldStatus = true
        var finalStatus = true
        documents.forEach { it ->
            val isValid = !it.documentUrl.isNullOrEmpty()

            if (it.nature == AppConstants.KEY_MANDATORY && isValid) {
                fieldStatus = true
            } else if (it.nature == AppConstants.KEY_MANDATORY && !isValid) {
                return false
            } else if (it.nature != AppConstants.KEY_MANDATORY) {
                fieldStatus = true
            }
            if (!fieldStatus) {
                finalStatus = fieldStatus
            }
            if (it.coordinates_required!!) {
                if (it.latitude.isNullOrEmpty() || it.longitude.isNullOrEmpty()) {
                    return false
                }
            }
        }
        return finalStatus
    }

    private fun createPickerDialog(position: Int) {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.image_picker)
        val camera: Button = dialog.findViewById(R.id.camera)
        val gallery: Button = dialog.findViewById(R.id.gallary)
        camera.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly().start()
//            ImagePicker.cameraOnly().start(this)
            caseObj.documents_business_attributes[position].isManualLatLng = false
            dialog.hide()
        }
        gallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly().start()
//            ImagePicker.create(this) // Activity or Fragment
//                    .showCamera(false).single().start()
            caseObj.documents_business_attributes[position].isManualLatLng = true
            dialog.hide()
        }
        dialog.show()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.clear()
        super.onViewStateRestored(savedInstanceState)
    }

}