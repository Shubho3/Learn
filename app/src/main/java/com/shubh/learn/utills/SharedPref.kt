package com.shubh.learn.utills

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.shubh.learn.dto.LoginDto


class SharedPref(context: Context) {

    private val PREFS_NAME = "app_name"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUserDetails(Key: String, loginModel: LoginDto) {
        val gson = Gson()
        val json: String = gson.toJson(loginModel)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(Key, json)
        editor.apply()
    }

    fun getUserDetails(Key: String): LoginDto {
        val gson = Gson()
        val json: String? = sharedPref.getString(Key, "{}")
        return gson.fromJson(json, LoginDto::class.java)
    }
/*
    fun setChildDetails(Key: String, loginModel: ChildData) {
        val gson = Gson()
        val json: String = gson.toJson(loginModel)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(Key, json)
        editor.apply()
    }*/

    /*fun getChildDetails(Key: String): ChildData {
        val gson = Gson()
        val json: String? = sharedPref.getString(Key, "{}")
        return gson.fromJson(json, ChildData::class.java)
    }
*/
    fun setBooleanValue(key: String?, value: Boolean) {
        val myPrefEditor: SharedPreferences.Editor = sharedPref.edit()
        myPrefEditor.putBoolean(key, value)
        myPrefEditor.apply()
    }

    fun getBooleanValue(key: String?): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun setStringValue(key: String?, value: String) {
        val myPrefEditor: SharedPreferences.Editor = sharedPref.edit()
        myPrefEditor.putString(key, value)
        myPrefEditor.apply()
    }

    fun getStringValue(key: String?): String? {
        return sharedPref.getString(key, "")
    }

    fun clearAllPreferences() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

}