package com.fede987.statusbaralert.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics

class ScreenUtils {

    companion object {

        private var titleBarHeigh = 0
        private val dipsMap: MutableMap<Float, Int> = mutableMapOf()

        fun convertDpToPixel(dp: Float, context: Context): Int {

            if (dipsMap.containsKey(dp)) {
                return dipsMap[dp]!!
            }

            val resources = context.resources
            val metrics = resources.displayMetrics
            val value = (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
            dipsMap[dp] = value

            return value
        }


        fun getStatusBarHeight(any: Activity): Int {

            if (titleBarHeigh > 0) {
                return titleBarHeigh
            }

            val resourceId = any.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                titleBarHeigh = any.resources.getDimensionPixelSize(resourceId)
            } else {
                titleBarHeigh = convertDpToPixel(25f, any)
            }

            return titleBarHeigh

        }

    }

}