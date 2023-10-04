package com.example.mainapplabrats.model


data class Cell(
        var id: Int,
        var nama: String,
        var penjelasan: String?,
        var tanda: String,
        var penyebab: String,
        var pencegahan: String,
        var rekomendasi: String
)