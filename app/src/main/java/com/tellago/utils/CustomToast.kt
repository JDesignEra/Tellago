package com.tellago.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.tellago.R

class CustomToast(context: Context, msg: String) {
    val c = context
    val m = msg

    fun primary() {
        toast(Color.WHITE, c.resources.getColor(R.color.toastPrimary))
    }

    fun success() {
        toast(Color.WHITE, c.resources.getColor(R.color.toastSuccess))
    }

    private fun toast(txtColor: Int, bgColor: Int) {
        val toast = Toast.makeText(
            c,
            m,
            Toast.LENGTH_SHORT
        )
        val toastView = toast.view
        val toastTxt = toastView.findViewById<TextView>(android.R.id.message)

        toast.setGravity(Gravity.TOP or Gravity.RIGHT, 0, 50)
        toastView.setBackgroundColor(bgColor)
        toastTxt.setTextColor(txtColor)

        toast.show()
    }
}