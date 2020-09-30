package com.tellago.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getColor
import com.muddzdev.styleabletoast.StyleableToast
import com.tellago.R

class CustomToast(private val context: Context) {
    private val toast = StyleableToast.Builder(context)

    fun success(msg: String, iconStart: Int? = null) {
        toast.text(msg)
            .backgroundColor(getColor(context, R.color.toastSuccess))
            .textColor(Color.WHITE)
            .iconStart(iconStart ?: R.drawable.ic_info_24_white).show()
    }

    fun error(msg: String, iconStart: Int? = null) {
        toast.text(msg)
            .backgroundColor(getColor(context, R.color.toastError))
            .textColor(Color.WHITE)
            .iconStart(iconStart ?: R.drawable.ic_info_24_white)
            .show()
    }
}