package com.example.mainapplabrats.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log


object DataLocal{
    private val IS_APP_INSTALLED_KEY = "is_app_installed"
    private val TAG : String = "CHECK_RESPONE"

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        Log.i(TAG,"KONVERT GAMBAR")
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    fun saveImageToSharedPreferences(imageBitmap: Bitmap?,context: Context) {
        if (imageBitmap != null) {
            Log.i(TAG,"SAVE GAMBAR")

            val imageBase64 = bitmapToBase64(imageBitmap)
            val sharedPref = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("TemporaryImageBase64", imageBase64)
            editor.apply()

        }
    }

     fun loadImageFromSharedPreferences(context: Context): Bitmap? {
        val sharedPref = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val imageBase64 = sharedPref.getString("TemporaryImageBase64", null)

        if (imageBase64 != null) {
            Log.i(TAG,"LOAD GAMBAR")
            val byteArray = Base64.decode(imageBase64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        return null
    }
    fun isAppInstalledBefore(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(IS_APP_INSTALLED_KEY, false)
    }

    fun markAppAsInstalled(context: Context) {
        val sharedPref = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(IS_APP_INSTALLED_KEY, true)
        editor.apply()
    }

}