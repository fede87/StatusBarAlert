package com.fede987.statusbaralert

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.fede987.statusbaralert.utils.ScreenUtils

/**
 * Created by fede987 on 05/04/18.
 */

class StatusBarAlert {

    class Builder(private var context: Activity) {

        private var text: Int = 0
        private var stringText: String = ""
        private var alertColor: Int = 0
        private var showProgress: Boolean = false
        private var duration:Long = 2000
        private var autoHide: Boolean = true

        fun build() : View? = addStatusBarTextAndProgress(context, text, stringText, alertColor,showProgress,autoHide, duration)

        fun withAlertColor(alertColor: Int): Builder {
            this.alertColor = alertColor
            return this
        }

        fun withText(text: Int): Builder {
            this.text = text
            return this
        }

        fun withText(text: String): Builder {
            this.stringText = text
            return this
        }

        fun showProgress(showProgress: Boolean): Builder {
            this.showProgress = showProgress
            return this
        }

        fun autoHide(autoHide: Boolean): Builder {
            this.autoHide = autoHide
            return this
        }

        fun withDuration(millis: Long): Builder {
            this.duration = millis
            return this
        }

    }

    companion object {

        private val allAlerts: MutableMap<String, MutableList<LinearLayout>?> = mutableMapOf()

        private var statusBarColorOringinal: Int = 0
        private var hasOriginalStatusBarTranslucent: Boolean = false

        fun addStatusBarTextAndProgress(any: Activity, text: Int?, stringText: String?, alertColor: Int, showProgress: Boolean, autoHide: Boolean, duration: Long): View? {

            hide(any,null)

            val decor = any.window.decorView as ViewGroup

            val statusBarAlert = LinearLayout(any)
            statusBarAlert.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusBarHeight(any))
            statusBarAlert.gravity = Gravity.CENTER_HORIZONTAL
            if(alertColor>0) {statusBarAlert.setBackgroundColor(ContextCompat.getColor(any, alertColor))}

            val ll2 = LinearLayout(any)
            ll2.orientation = LinearLayout.HORIZONTAL
            ll2.gravity = Gravity.CENTER_VERTICAL
            ll2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.getStatusBarHeight(any))

            val t = TextView(any)
            t.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.getStatusBarHeight(any))
            t.textSize = 11f
            t.setTextColor(Color.WHITE)
            t.gravity = Gravity.CENTER
            t.text = if (text!=0) any.resources.getString(text!!) + " " else if(stringText!="") "$stringText " else ""
            t.includeFontPadding = false
            ll2.addView(t)

            if (showProgress) {
                val p = ProgressBar(any)
                p.isIndeterminate = true
                p.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                p.layoutParams = ViewGroup.LayoutParams(ScreenUtils.convertDpToPixel(11f, any), ScreenUtils.convertDpToPixel(11f, any))
                ll2.addView(p)
            }

            statusBarAlert.addView(ll2)

            val decorView = any.window.decorView.rootView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE

            hasOriginalStatusBarTranslucent = isTranslucentStatusBar(any)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                any.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColorOringinal = any.window.statusBarColor
                any.window.statusBarColor = Color.TRANSPARENT
            }

            decor.addView(statusBarAlert)

            ll2.translationY = -ScreenUtils.convertDpToPixel(25f,any).toFloat()


            ll2.animate()!!
                    .translationY(0f)
                    .setDuration(150)
                    .setStartDelay(350)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()

            if(autoHide) {
                statusBarAlert.postDelayed({
                    if(!any.isFinishing && statusBarAlert.parent!=null) {
                        hideInternal(any, statusBarAlert,null)
                    }
                    allAlerts.remove(any.componentName.className)
                },duration+500)
            }

            if(allAlerts[any.componentName.className]==null) {
                allAlerts[any.componentName.className] = mutableListOf()
            }

            allAlerts[any.componentName.className]?.add(statusBarAlert)

            return statusBarAlert
        }

        fun hide(any: Activity, onHidden: Runnable?) {

            if(allAlerts[any.componentName.className]==null || allAlerts[any.componentName.className]?.size==0) {

                onHidden?.run()

            } else {

                allAlerts[any.componentName.className]?.forEach {

                   hideInternal(any,it,onHidden)

                }

                allAlerts[any.componentName.className]?.clear()

            }

        }

        private fun hideInternal(any: Activity, it: LinearLayout, onHidden: Runnable?) {

            if(it.parent != null) {

                any.window.decorView.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    any.window.statusBarColor = statusBarColorOringinal

                    if(hasOriginalStatusBarTranslucent) {
                        any.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    }
                }

                val decor = any.window.decorView as ViewGroup

                it.animate()
                        ?.translationY(-ScreenUtils.convertDpToPixel(25f, any).toFloat())
                        ?.setDuration(150)
                        ?.setStartDelay(500)
                        ?.setInterpolator(AccelerateInterpolator())
                        ?.setListener(object: Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationEnd(animation: Animator?) {
                                decor.removeView(it)
                                onHidden?.run()
                            }
                            override fun onAnimationStart(animation: Animator?) {}
                            override fun onAnimationCancel(animation: Animator?) {}
                        })
                        ?.start()

            }

        }

        private fun isTranslucentStatusBar(any: Context): Boolean {
            val w = (any as Activity).window
            val lp = w.attributes
            val flags = lp.flags
            // Here I'm comparing the binary value of Translucent Status Bar with flags in the window
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            } else {
                return false
            }

        }

    }

}