package com.example.retrofitassignment.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MarsPhoto(
    val id: String,
    @SerializedName("img_src")
    val imgSrc: String
)