package com.devvikram.chatmate.models

import android.os.Parcel
import android.os.Parcelable

data class DocumentModel(
    val fileName: String,
    val uri: String,
    val fileType: String,
    val isDeleteEnabled: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileName)
        parcel.writeString(uri)
        parcel.writeString(fileType)
        parcel.writeByte(if (isDeleteEnabled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocumentModel> {
        override fun createFromParcel(parcel: Parcel): DocumentModel {
            return DocumentModel(parcel)
        }

        override fun newArray(size: Int): Array<DocumentModel?> {
            return arrayOfNulls(size)
        }
    }
}
