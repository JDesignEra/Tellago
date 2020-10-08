package com.tellago.utilities

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getColor
import com.muddzdev.styleabletoast.StyleableToast
import com.tellago.R

class CustomToast(private val context: Context) : StyleableToast.Builder(context) {
    fun success(msg: String,
                iconStart: Int? = R.drawable.ic_info_24_white,
                gravity: Int? = null,
                cornerRadius: Int? = null,
                solidBackground: Boolean = false
    ) {
        this.text(msg).apply {
            backgroundColor(getColor(context, R.color.toastSuccess))
            textColor(Color.WHITE)

            if (iconStart != null) iconStart(iconStart)
            if (gravity != null) gravity(gravity)
            if (cornerRadius != null) cornerRadius(cornerRadius)
            if (solidBackground) solidBackground()
        }.show()
    }

    fun error(msg: String,
              iconStart: Int? = R.drawable.ic_info_24_white,
              gravity: Int? = null,
              cornerRadius: Int? = null,
              solidBackground: Boolean = false
    ) {
        this.text(msg).apply {
            backgroundColor(getColor(context, R.color.toastError))
            textColor(Color.WHITE)

            if (iconStart != null) iconStart(iconStart)
            if (gravity != null) gravity(gravity)
            if (cornerRadius != null) cornerRadius(cornerRadius)
            if (solidBackground) solidBackground()
        }.show()
    }
}