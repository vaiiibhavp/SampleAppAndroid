package com.example.sampleapp.modal.gymListResponce

import com.google.gson.annotations.SerializedName

data class GymListData(
    @SerializedName("ID") var ID: Int? = null,
    @SerializedName("Name") var Name: String? = null,
    @SerializedName("GymType") var GymType: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("Country") var Country: String? = null,
    @SerializedName("Address") var Address: String? = null,
    @SerializedName("Lat") var Lat: Double? = null,
    @SerializedName("Long") var Long: Double? = null,
    @SerializedName("Amenities") var Amenities: String? = null,
    @SerializedName("CreatedOn") var CreatedOn: String? = null,
    @SerializedName("ModifiedOn") var ModifiedOn: String? = null,
    @SerializedName("ImageType") var ImageType: String? = null,
    @SerializedName("Label") var Label: String? = null,
    @SerializedName("Type") var Type: String? = null,
    @SerializedName("Logo") var Logo: String? = null
)