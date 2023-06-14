package com.shubh.learn.utills

import android.util.Patterns

object EmailValidation {

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}