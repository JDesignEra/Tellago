package com.tellago.utilities

import android.content.Context
import android.widget.NumberPicker

class NumPickerUtils(context: Context?) : NumberPicker(context) {
    fun animateValueByOne(picker: NumberPicker, increment: Boolean = true) {
        val func = picker::class.java.getDeclaredMethod("changeValueByOne", increment::class.java)
        func.isAccessible = true
        func.invoke(picker, increment)
    }
}