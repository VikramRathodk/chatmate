package com.devvikram.chatmate.db

import androidx.room.TypeConverter
import com.devvikram.chatmate.models.DocumentModel
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromDocumentModel(model:DocumentModel) : String?{
        return Gson().toJson(model)
    }
    @TypeConverter
    fun toDocumentModel(value:String) : DocumentModel?{
        return Gson().fromJson(value, DocumentModel::class.java)
    }



}