package com.fede987.statusbaralert

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fede987.statusbaralert.utils.convertDpToPixel
import com.fede987.statusbaralert.utils.getStatusBarHeight

@SuppressLint("ViewConstructor")
class StatusBarAlertView(
        any: Activity,
        alertColor: Int,
        stringText: String?,
        text: Int?,
        textColor: Int,
        typeface: Typeface?,
        showProgress: Boolean,
        indeterminateProgressBarColor: Int,
        autohide: Boolean,
        autohideDuration: Long)
    : LinearLayout(any, null, 0) {

    var statusBarColorOringinal: Int = 0
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var autohideRunnable: Runnable? = null

    init {
        this.observeLifecycle(any)
        this.buildUI(
                any,
                alertColor,
                stringText,
                text,
                textColor,
                typeface,
                showProgress,
                indeterminateProgressBarColor,
                autohide,
                autohideDuration)
    }

    private fun observeLifecycle(any: Context) {
        if (any is AppCompatActivity) {
            any.lifecycle.addObserver(object : LifecycleObserver {

                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun destroy() {
                    StatusBarAlert.hide(any)
                    any.lifecycle.removeObserver(this)
                }
            })
        }
    }

    private fun buildUI(
            any: Activity,
            alertColor: Int,
            stringText: String?,
            text: Int?,
            textColor: Int,
            typeFace: Typeface?,
            showProgress: Boolean,
            indeterminateProgressBarColor: Int,
            autohide: Boolean,
            autohideDuration: Long) {

        val isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        val decorView = (any as? Activity)!!.window.decorView as ViewGroup

        val statusBarHeight = any.getStatusBarHeight() * (if (isLollipop) 1 else 2)

        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
        this.gravity = Gravity.CENTER_HORIZONTAL

        val wrapper = LinearLayout(any)
        wrapper.orientation = HORIZONTAL
        wrapper.gravity = Gravity.CENTER
        wrapper.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
        wrapper.setPadding(0, if (isLollipop) 0 else statusBarHeight / 2, 0, 0)
        if (alertColor > 0) wrapper.setBackgroundColor(ContextCompat.getColor(any, alertColor))

        textView = TextView(any)
        textView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, statusBarHeight)
        textView!!.textSize = 11f
        textView!!.setTextColor(Color.WHITE)
        textView!!.gravity = Gravity.CENTER
        if (text != null) textView?.text = if (text != 0) any.resources.getString(text) + " " else if (stringText != "") "$stringText " else ""

        if (textColor > 0) textView!!.setTextColor(ContextCompat.getColor(any, textColor))

        textView!!.includeFontPadding = false
        typeFace?.let { textView?.typeface = it }
        wrapper.addView(textView)

        progressBar = ProgressBar(any)
        if (indeterminateProgressBarColor > 0) {
            @Suppress("DEPRECATION")
            @SuppressLint("NewApi")
            if (isLollipop) {
                progressBar!!.indeterminateTintMode = PorterDuff.Mode.SRC_IN
                progressBar!!.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(any, indeterminateProgressBarColor))
            } else progressBar!!.indeterminateDrawable.setColorFilter(ContextCompat.getColor(any, indeterminateProgressBarColor), PorterDuff.Mode.SRC_IN)
        }
        progressBar!!.isIndeterminate = true
        val textSize = any.convertDpToPixel(11f)
        progressBar!!.layoutParams = ViewGroup.LayoutParams(textSize, textSize)
        wrapper.addView(progressBar)
        if (showProgress) progressBar?.visibility = View.VISIBLE
        else progressBar?.visibility = View.GONE
        addView(wrapper)

        val lowProfileSystemUIVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
        decorView.systemUiVisibility = lowProfileSystemUIVisibility
        decorView.setOnSystemUiVisibilityChangeListener { _ ->
            this.systemUiVisibility = lowProfileSystemUIVisibility
        }

        @SuppressLint("NewApi")
        if (isLollipop) statusBarColorOringinal = any.window.statusBarColor

        decorView.post { decorView.addView(this) }

        wrapper.translationY = -statusBarHeight.toFloat()

        wrapper.animate()!!
                .translationY(0f)
                .setDuration(any.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

        if (autohide) {
            autohideRunnable = Runnable {
                StatusBarAlert.hide(any)
                StatusBarAlert.allAlerts.remove(any.componentName.className)
            }
            postDelayed(autohideRunnable, autohideDuration)
        }
    }

    override fun onDetachedFromWindow() {
        autohideRunnable?.let { removeCallbacks(it) }
        autohideRunnable = null
        (context as Activity).window.decorView.setOnSystemUiVisibilityChangeListener(null)
        super.onDetachedFromWindow()
    }

    /**
     * Update text with new string
     * @param text string
     */
    fun updateText(text: String) {
        textView?.text = "$text "
    }

    /**
     * Update text with string resource
     * @param text string res
     */
    fun updateText(text: Int) {
        textView?.text = """${context.resources.getString(text)} """
    }

    /**
     * Show indeterminate progress
     */
    fun showIndeterminateProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    /**
     * Hide indeterminate progress
     */
    fun hideIndeterminateProgress() {
        progressBar?.visibility = View.GONE
    }
}