package com.mmushtaq.bank.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Documents : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("document_url")
    @Expose
    var documentUrl: String? = null

    @SerializedName("nature")
    @Expose
    var nature: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("document_type")
    @Expose
    var documentType: String? = null

    var isAdded: Boolean? = false

    var isManualLatLng: Boolean? = false

    @SerializedName("coordinates_required")
    @Expose
    var coordinates_required : Boolean? = false

    @SerializedName("longitude")
    @Expose
    var longitude : String? = null

    @SerializedName("latitude")
    @Expose
    var latitude : String? = null
}