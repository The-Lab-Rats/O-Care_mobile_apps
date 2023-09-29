package com.example.mainapplabrats.model

import android.os.Parcel
import android.os.Parcelable


data class Cell(
        var id: Int,
        var nama: String,
        var penyebab: String,
        var rekomendasi: String
): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)
                parcel.writeString(nama)
                parcel.writeString(penyebab)
                parcel.writeString(rekomendasi)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Cell> {
                override fun createFromParcel(parcel: Parcel): Cell {
                        return Cell(parcel)
                }

                override fun newArray(size: Int): Array<Cell?> {
                        return arrayOfNulls(size)
                }
        }
}