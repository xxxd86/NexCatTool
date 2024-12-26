package com.nexify.layoutspeclibrary.utils

import android.content.Context

fun dp2px(context: Context, dpValue: Int): Int {
    return (context.resources.displayMetrics.density * dpValue + 0.5f).toInt()
}

fun dp2px_F(context: Context, dpValue: Int): Float {
    return (context.resources.displayMetrics.density * dpValue + 0.5f).toFloat()
}

fun convertIntToDp(context: Context, dpValue: Int): Float {
    return (  dpValue / context.resources.displayMetrics.density + 0.5f)
}