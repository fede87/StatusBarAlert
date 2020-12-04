package com.fede987.statusbaralert

import android.animation.Animator
import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import com.fede987.statusbaralert.utils.getStatusBarHeight

/**
 * Created by fede987 on 05/04/18.
 */

class StatusBarAlert {

    /**
     * Status bar alert builder.
     * @param context activity context for the status bar alert creation.
     */
    class Builder(private var context: Activity) {

        private var text: Int = 0
        private var stringText: String = ""
        private var alertColor: Int = 0
        private var showProgress: Boolean = false
        private var duration: Long = 2000
        private var autoHide: Boolean = true
        private var typeFace: Typeface? = null

        /**
         * Adds alert background color.
         * @param alertColor background color.
         * @return Builder
         */
        fun withAlertColor(alertColor: Int): Builder {
            this.alertColor = alertColor
            return this
        }

        /**
         * Sets status bar text.
         * @param text status bar text string resource.
         * @return Builder
         */
        fun withText(text: Int): Builder {
            this.text = text
            return this
        }

        /**
         * Sets status bar text.
         * @param text status bar text string.
         * @return Builder
         */
        fun withText(text: String): Builder {
            this.stringText = text
            return this
        }

        /**
         * Enables status bar indeterminate progress bar.
         * @param showProgress
         * @return Builder
         */
        fun showProgress(showProgress: Boolean): Builder {
            this.showProgress = showProgress
            return this
        }

        /**
         * Enables autohide after status bar alert has been shown.
         * @param autoHide
         * @return Builder
         */
        fun autoHide(autoHide: Boolean): Builder {
            this.autoHide = autoHide
            return this
        }

        /**
         * Sets custom duration before status bar alert is going to be hidden.
         * @param millis milliseconds before hiding.
         * @return Builder
         */
        fun withDuration(millis: Long): Builder {
            this.duration = millis
            return this
        }

        /**
         * Sets custom typeface for label.
         * @param typeface custom typeface.
         * @return Builder
         */
        fun withTypeface(typeface: Typeface): Builder {
            this.typeFace = typeFace
            return this
        }

        /**
         * Builds and return status bar alert as a View.
         * @return view status bar alert.
         */
        fun build(): StatusBarAlertView? = addStatusBarTextAndProgress(context, text, stringText, alertColor, showProgress, typeFace, autoHide, duration)
    }

    companion object {

        @JvmField
        val allAlerts: MutableMap<String, MutableList<StatusBarAlertView>?> = mutableMapOf()

        internal fun addStatusBarTextAndProgress(any: Activity, text: Int?, stringText: String?, alertColor: Int, showProgress: Boolean, typeFace: Typeface?, autoHide: Boolean, duration: Long): StatusBarAlertView? {

            this.hide(any)

            val statusBarAlert = StatusBarAlertView(any, alertColor, stringText, text, typeFace, showProgress, autoHide, duration)

            if (allAlerts[any.componentName.className] == null) {
                allAlerts[any.componentName.className] = mutableListOf()
            }

            allAlerts[any.componentName.className]?.add(statusBarAlert)


            return statusBarAlert
        }

        @Deprecated(
                message = "Use new hide implementation hide(any: Activity, onHidden: (() -> Unit)?)",
                level = DeprecationLevel.WARNING,
                replaceWith = ReplaceWith(expression = "StatusBarAlert.hide(activity) {}"))
        fun hide(any: Activity, onHidden: Runnable?) {

            if (allAlerts[any.componentName.className] == null || allAlerts[any.componentName.className]?.size == 0) {
                onHidden?.run()
            } else {
                allAlerts[any.componentName.className]?.forEach {
                    hideInternal(any, it, onHidden)
                }
                allAlerts[any.componentName.className]?.clear()
            }
        }

        fun hide(any: Activity, onHidden: (() -> Unit)? = null) {

            if (allAlerts[any.componentName.className] == null || allAlerts[any.componentName.className]?.size == 0) {
                onHidden?.invoke()
            } else {
                allAlerts[any.componentName.className]?.forEach {
                    hideInternal(any, it, null, onHidden)
                }
                allAlerts[any.componentName.className]?.clear()
            }
        }

        private fun hideInternal(any: Activity, it: StatusBarAlertView, onHiddenRunnable: Runnable? = null, onHidden: (() -> Unit)? = null) {

            if (it.parent != null) {

                any.window.decorView.rootView.systemUiVisibility =
                        any.window.decorView.rootView.systemUiVisibility or View.SYSTEM_UI_FLAG_VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    any.window.statusBarColor = it.statusBarColorOringinal
                    if (it.hasOriginalStatusBarTranslucent) {
                        any.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    }
                }

                val decor = any.window.decorView as ViewGroup

                it.animate()
                        ?.translationY(-any.getStatusBarHeight().toFloat())
                        ?.setDuration(150)
                        ?.setStartDelay(500)
                        ?.setInterpolator(AccelerateInterpolator())
                        ?.setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationEnd(animation: Animator?) {
                                decor.removeView(it)
                                onHiddenRunnable?.run()
                                onHidden?.invoke()
                            }

                            override fun onAnimationStart(animation: Animator?) {}
                            override fun onAnimationCancel(animation: Animator?) {}
                        })
                        ?.start()
            }
        }
    }
}