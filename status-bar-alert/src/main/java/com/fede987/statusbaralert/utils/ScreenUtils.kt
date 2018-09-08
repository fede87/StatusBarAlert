package com.fede987.statusbaralert.utils

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

private var titleBarHeigh = 0
private val dipsMap: MutableMap<Float, Int> = mutableMapOf()

internal fun Activity.convertDpToPixel(dp: Float): Int {

    if (dipsMap.containsKey(dp)) {
        return dipsMap[dp]!!
    }

    val resources = this.resources
    val metrics = resources.displayMetrics
    val value = (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    dipsMap[dp] = value

    return value
}

internal fun Activity.getStatusBarHeight(): Int {

    if (titleBarHeigh > 0) {
        return titleBarHeigh
    }

    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        titleBarHeigh = this.resources.getDimensionPixelSize(resourceId)
    } else {
        titleBarHeigh = convertDpToPixel(25f)
    }

    return titleBarHeigh
}

internal fun Activity.isTranslucentStatusBar(): Boolean {
    val w = this.window
    val lp = w.attributes
    val flags = lp.flags
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    } else {
        return false
    }
}