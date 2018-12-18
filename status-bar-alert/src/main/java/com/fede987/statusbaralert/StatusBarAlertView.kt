package com.fede987.statusbaralert

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.fede987.statusbaralert.utils.convertDpToPixel
import com.fede987.statusbaralert.utils.getStatusBarHeight
import com.fede987.statusbaralert.utils.isTranslucentStatusBar

@SuppressLint("ViewConstructor")
class StatusBarAlertView(any: Activity, alertColor: Int, stringText: String?, text: Int?, typeface: Typeface?, showProgress: Boolean, autohide: Boolean, autohideDuration: Long)
    : LinearLayout(any, null,0) {

    var statusBarColorOringinal: Int = 0
    var hasOriginalStatusBarTranslucent: Boolean = false
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null
    private var autohideRunnable: Runnable? = null

    init {
        this.observeLifecycle(any)
        this.buildUI(any,alertColor,stringText,text,typeface,showProgress,autohide,autohideDuration)
    }

    private fun observeLifecycle(any: Context) {
        if(any is AppCompatActivity) {
            any.lifecycle.addObserver(object : LifecycleObserver{

                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun destroy() {
                    StatusBarAlert.hide(any, Runnable{})
                    any.lifecycle.removeObserver(this)
                }
            })
        }
    }

    private fun buildUI(any: Activity, alertColor: Int, stringText: String?, text: Int?, typeFace: Typeface?, showProgress: Boolean, autohide: Boolean, autohideDuration: Long) {

        val decor = (any as? Activity)!!.window.decorView as ViewGroup

        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, any.getStatusBarHeight())
        this.gravity = Gravity.CENTER_HORIZONTAL
        setBackgroundColor(alertColor)

        val ll2 = LinearLayout(any)
        ll2.orientation = LinearLayout.HORIZONTAL
        ll2.gravity = Gravity.CENTER_VERTICAL
        ll2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, any.getStatusBarHeight())

        textView = TextView(any)
        textView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, any.getStatusBarHeight())
        textView?.textSize = 11f
        textView?.setTextColor(Color.WHITE)
        textView?.gravity = Gravity.CENTER
        if(text!=null) {
            textView?.text = if (text!=0) any.resources.getString(text) + " " else if(stringText!="") "$stringText " else ""
        }
        textView?.includeFontPadding = false
        typeFace?.let { textView?.typeface = it }
        ll2.addView(textView)

        progressBar = ProgressBar(any)
        progressBar?.isIndeterminate = true
        progressBar?.indeterminateDrawable?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        progressBar?.layoutParams = ViewGroup.LayoutParams(any.convertDpToPixel(11f), any.convertDpToPixel(11f))
        ll2.addView(progressBar)
        if (showProgress)
            progressBar?.visibility = View.VISIBLE
        else
            progressBar?.visibility = View.GONE
        addView(ll2)

        val decorView = any.window.decorView.rootView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE

        decorView.setOnSystemUiVisibilityChangeListener { _ ->
            this.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        }

        hasOriginalStatusBarTranslucent = any.isTranslucentStatusBar()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            any.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColorOringinal = any.window.statusBarColor
            any.window.statusBarColor = Color.TRANSPARENT

        }

        decor.addView(this)

        ll2.translationY = -any.getStatusBarHeight().toFloat()

        ll2.animate()!!
                .translationY(0f)
                .setDuration(150)
                .setStartDelay(350)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

        if(autohide) {

            autohideRunnable = Runnable{
                StatusBarAlert.hide(any, null)
                StatusBarAlert.allAlerts.remove(any.componentName.className)
            }

            postDelayed(autohideRunnable,autohideDuration+500)
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