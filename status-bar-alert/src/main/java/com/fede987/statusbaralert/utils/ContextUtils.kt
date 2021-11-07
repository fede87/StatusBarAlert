package com.fede987.statusbaralert.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fede987.statusbaralert.StatusBarAlert

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