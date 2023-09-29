package com.example.mainapplabrats.model

import com.google.gson.annotations.SerializedName

data class ModelDiagnosa(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("penjelasan")
    val penjelasan: String,
    @SerializedName("tanda")
    val tanda: String,
    @SerializedName("penyebab")
    val penyebab: String,
    @SerializedName("pencegahan")
    val pencegahan: String,
    @SerializedName("rekomendasi")
    val rekomendasi: String
)

