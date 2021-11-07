package com.fede987.statusbaralert.utils

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fede987.statusbaralert.StatusBarAlert

internal fun Context.getColorSafe(color: Int, isColor: Boolean): Int {
	return if (!isColor) {
		try {
			ContextCompat.getColor(this, color)
		} catch (ignored: Exception) {
			color
		}
	} else {
		color
	}
}

/**
 * Extension function providing a Type-safe builder for StatusBarAlert in Activities
 *
 * @see StatusBarAlert
 */
fun Activity.statusBarAlert(
	builder: StatusBarAlert.Builder.() -> Unit
) = StatusBarAlert.Builder(this).apply(builder).build()

/**
 * Extension function providing a Type-safe builder for StatusBarAlert in FragmentActivities
 *
 * @see StatusBarAlert
 */
fun FragmentActivity.statusBarAlert(
	builder: StatusBarAlert.Builder.() -> Unit
) = StatusBarAlert.Builder(this).apply(builder).build()

/**
 * Extension function providing a Type-safe builder for StatusBarAlert in Fragments
 *
 * @see StatusBarAlert
 */
fun Fragment.statusBarAlert(
	builder: StatusBarAlert.Builder.() -> Unit
) = StatusBarAlert.Builder(this).apply(builder).build()