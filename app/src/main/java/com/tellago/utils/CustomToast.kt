package com.tellago.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import com.tellago.R

class CustomToast(private val context: Context) {
    private val toast = Toast.makeText(
        context,
        null,
        Toast.LENGTH_SHORT
    )

    fun error(msg: String) {
        showToast(Color.WHITE, getColor(context, R.color.toastError), msg)
    }

    fun success(msg: String) {
        showToast(Color.WHITE, getColor(context, R.color.toastSuccess), msg)
    }

    private fun showToast(txtColor: Int, bgColor: Int, msg: String) {
        val toastView = toast.view
        val toastTxt = toastView.findViewById<TextView>(android.R.id.message)

        toast.setText(msg)
        toast.setGravity(Gravity.TOP or Gravity.END, 0, 300)

        toastView.setBackgroundColor(bgColor)
        toastTxt.setTextColor(txtColor)

        toast.show()
    }
}