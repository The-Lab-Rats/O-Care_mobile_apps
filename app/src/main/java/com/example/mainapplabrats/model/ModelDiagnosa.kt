package com.example.mainapplabrats.model

import com.google.gson.annotations.SerializedName

data class ModelDiagnosa(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("penyebab")
    val penyebab: String,
    @SerializedName("rekomendasi")
    val rekomendasi: String
)

