package com.e.kalaka.utils

import android.text.TextUtils

class Validation() {




    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}