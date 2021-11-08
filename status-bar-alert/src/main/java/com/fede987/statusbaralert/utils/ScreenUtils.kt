package com.fede987.statusbaralert.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

private var titleBarHeigh = 0
private val dipsMap: MutableMap<Float, Int> = mutableMapOf()

internal fun Context.convertDpToPixel(dp: Float): Int {
    if (dipsMap.containsKey(dp)) return dipsMap[dp]!!
    val resources = this.resources
    val metrics = resources.displayMetrics
    val value = (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    dipsMap[dp] = value
    return value
}

internal fun Context.getStatusBarHeight(): Int {
    if (titleBarHeigh > 0) return titleBarHeigh
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    titleBarHeigh = if (resourceId > 0) {
        this.resources.getDimensionPixelSize(resourceId)
    } else convertDpToPixel(25f)
    return titleBarHeigh
}