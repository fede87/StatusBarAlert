package com.fede987.statusbaralert

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fede987.statusbaralert.utils.convertDpToPixel
import com.fede987.statusbaralert.utils.getStatusBarHeight
import com.fede987.statusbaralert.utils.getColorSafe
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

@SuppressLint("ViewConstructor")
class StatusBarAlert @JvmOverloads internal constructor(
	private val activity: Activity,
	private val alertColor: Int = Color.TRANSPARENT,
	private val text: String = String(),
	private val textColor: Int = Color.WHITE,
	private val typeFace: Typeface? = null,
	private val showProgress: Boolean = false,
	private val progressBarColor: Int = Color.WHITE,
	private var autoHide: Boolean = false,
	private var duration: Long = 0L
) : LinearLayoutCompat(activity) {
	
	private var textView: TextView? = null
	private var progressBar: ProgressBar? = null
	private var wrapper: LinearLayout? = null
	
	private var statusBarColorOriginal: Int = 0
	
	private val statusBarHeight
		get() = activity.getStatusBarHeight() * (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 1 else 2)
	
	private val decorView
		get() = activity.window?.decorView
	
	init {
		this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
		this.gravity = Gravity.CENTER_HORIZONTAL
		this.visibility = View.GONE
		
		observeLifecycle()
		buildWrapper()
	}
	
	private fun observeLifecycle() {
		if (activity is AppCompatActivity) {
			activity.lifecycle.addObserver(object : LifecycleObserver {
				
				@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
				fun destroy() {
					hide()
					activity.lifecycle.removeObserver(this)
				}
			})
		}
	}
	
	private fun buildWrapper() {
		wrapper = LinearLayout(activity)
		wrapper?.orientation = LinearLayout.HORIZONTAL
		wrapper?.gravity = Gravity.CENTER
		wrapper?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
		wrapper?.setPadding(0, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 0 else statusBarHeight / 2, 0, 0)
		wrapper?.setBackgroundColor(alertColor)
		
		val progressBarSize = activity.convertDpToPixel(11F)
		progressBar = ProgressBar(activity).apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				indeterminateTintMode = PorterDuff.Mode.SRC_IN
				indeterminateTintList = ColorStateList.valueOf(progressBarColor)
			} else {
				indeterminateDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(progressBarColor, BlendModeCompat.SRC_IN)
			}
			isIndeterminate = true
			layoutParams = ViewGroup.LayoutParams(progressBarSize, progressBarSize)
			visibility = if (showProgress) View.VISIBLE else View.GONE
		}
		wrapper?.addView(progressBar)
		
		textView = TextView(activity).apply {
			layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, statusBarHeight)
			textSize = 11F
			setTextColor(textColor)
			gravity = Gravity.CENTER
			text = this@StatusBarAlert.text
			includeFontPadding = false
			typeFace?.let { this.typeface = it }
		}
		wrapper?.addView(textView)
		addView(wrapper)
	}
	
	private fun animateWrapper() {
		wrapper?.animation?.cancel()
		wrapper?.animation?.reset()
		slideDown()
		
		if (autoHide) {
			hide()
		}
	}
	
	private fun slideDown() {
		ObjectAnimator.ofFloat(wrapper, View.TRANSLATION_Y, -statusBarHeight.toFloat(), 0F).apply {
			duration = activity.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
			interpolator = AccelerateDecelerateInterpolator()
			start()
		}
	}
	
	private fun slideUp(onEnd: (() -> Unit)? = null) {
		ObjectAnimator.ofFloat(wrapper, View.TRANSLATION_Y, 0F, -statusBarHeight.toFloat()).apply {
			startDelay = this@StatusBarAlert.duration
			duration = activity.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
			interpolator = AccelerateInterpolator()
			addListener(object : Animator.AnimatorListener{
				override fun onAnimationStart(animation: Animator?) { }
				override fun onAnimationRepeat(animation: Animator?) { }
				override fun onAnimationEnd(animation: Animator?) {
					onEnd?.invoke()
				}
				override fun onAnimationCancel(animation: Animator?) {
					onEnd?.invoke()
				}
			})
			start()
		}
	}
	
	/**
	 * Displays the StatusBarAlert.
	 * If any other is shown currently, it gets hid and then the new pops up.
	 *
	 * @return StatusBarAlertDsl this
	 * @see StatusBarAlert
	 */
	fun show(): StatusBarAlert {
		if (currentInstance.get() != null) {
			currentInstance.get()?.hide {
				showInternal()
			}
		} else {
			showInternal()
		}
		return this
	}
	
	private fun showInternal() {
		currentInstance = WeakReference(this)
		val lowProfileSystemUIVisibility = decorView?.systemUiVisibility
			?.or(View.SYSTEM_UI_FLAG_LOW_PROFILE)
			?: View.SYSTEM_UI_FLAG_LOW_PROFILE
		decorView?.systemUiVisibility = lowProfileSystemUIVisibility
		decorView?.setOnSystemUiVisibilityChangeListener { _ ->
			this.systemUiVisibility = lowProfileSystemUIVisibility
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			statusBarColorOriginal = activity.window.statusBarColor
		}
		
		wrapper?.translationY = -statusBarHeight.toFloat()
		this.visibility = View.VISIBLE
		decorView?.post { (decorView as? ViewGroup?)?.addView(this) }
		
		animateWrapper()
	}
	
	/**
	 * Hides the StatusBarAlert manually.
	 *
	 * @param onHidden listener which can be used to know when the alert disappears.
	 * @return StatusBarAlertDsl this
	 * @see StatusBarAlert
	 */
	@JvmOverloads
	fun hide(onHidden: (() -> Unit)? = null): StatusBarAlert {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			activity.window?.statusBarColor = statusBarColorOriginal
		}
		
		slideUp {
			decorView?.post {
				decorView?.systemUiVisibility = decorView?.systemUiVisibility
					?.and(View.SYSTEM_UI_FLAG_LOW_PROFILE.inv())
					?: View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
				(decorView as? ViewGroup?)?.removeView(this)
			}
			currentInstance.clear()
			onHidden?.invoke()
		}
		return this
	}
	
	/**
	 * Update the text with new CharSequence
	 *
	 * @param text the new CharSequence
	 * @see CharSequence
	 */
	fun setText(text: CharSequence) {
		textView?.text = text
	}
	
	/**
	 * Update the text with string resource
	 *
	 * @param stringId the id of the string resource
	 */
	fun setText(@StringRes stringId: Int) {
		textView?.text = activity.getString(stringId)
	}
	
	/**
	 * Update the text with string resource
	 *
	 * @param stringId the id of the string resource
	 * @param formatArgs args used to format the string
	 */
	fun setText(@StringRes stringId: Int, vararg formatArgs: Any) {
		textView?.text = activity.getString(stringId, formatArgs)
	}
	
	/**
	 * Show the ProgressBar
	 */
	fun showProgress() {
		progressBar?.visibility = View.VISIBLE
	}
	
	/**
	 * Hide the ProgressBar
	 */
	fun hideProgress() {
		progressBar?.visibility = View.GONE
	}
	
	/**
	 * Sets the StatusBarAlert background color
	 *
	 * @param color accepts ColorRes and ColorInt
	 * @param isColorInt helps to detect if the color is a resource or color int (not required)
	 */
	fun setAlertColor(color: Int, isColorInt: Boolean = false) {
		wrapper?.setBackgroundColor(activity.getColorSafe(color, isColorInt))
	}
	
	/**
	 * Sets the StatusBarAlert text color
	 *
	 * @param color accepts ColorRes and ColorInt
	 * @param isColorInt helps to detect if the color is a resource or color int (not required)
	 */
	fun setTextColor(color: Int, isColorInt: Boolean = false) {
		textView?.setTextColor(activity.getColorSafe(color, isColorInt))
	}
	
	/**
	 * Sets the StatusBarAlert progress bar color
	 *
	 * @param color accepts ColorRes and ColorInt
	 * @param isColorInt helps to detect if the color is a resource or color int (not required)
	 */
	fun setProgressBarColor(color: Int, isColorInt: Boolean = false) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			progressBar?.indeterminateTintMode = PorterDuff.Mode.SRC_IN
			progressBar?.indeterminateTintList = ColorStateList.valueOf(activity.getColorSafe(color, isColorInt))
		} else {
			progressBar?.indeterminateDrawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
				activity.getColorSafe(color, isColorInt),
				BlendModeCompat.SRC_IN
			)
		}
	}
	
	/**
	 * Sets custom duration before the StatusBarAlert is going to be hidden.
	 *
	 * @param millis milliseconds before hiding
	 */
	fun setDuration(millis: Long) {
		this.duration = millis
	}
	
	/**
	 * Sets custom duration before the StatusBarAlert is going to be hidden.
	 *
	 * @param time time before hiding
	 * @param unit TimeUnit which is used to associate the time with.
	 * @see TimeUnit
	 */
	fun setDuration(time: Long, unit: TimeUnit) {
		this.duration = TimeUnit.MILLISECONDS.convert(time, unit)
	}
	
	/**
	 * Enable or disable autoHide after StatusBarAlert has been shown.
	 *
	 * @param hide whether or not to hide it automatically
	 * @return Builder
	 */
	fun setAutoHide(autoHide: Boolean) {
		this.autoHide = autoHide
	}
	
	/**
	 * Sets custom typeface for the text.
	 *
	 * @param typeface the custom typeface
	 * @return Builder
	 */
	fun setTypeface(typeface: Typeface?) {
		textView?.typeface = typeface
	}
	
	override fun onDetachedFromWindow() {
		activity.window?.decorView?.setOnSystemUiVisibilityChangeListener(null)
		super.onDetachedFromWindow()
	}
	
	companion object {
		private var currentInstance: WeakReference<StatusBarAlert?> = WeakReference(null)
	}
	
	class Builder(private val activity: Activity) {
		private var text: String = String()
		private var autoHide: Boolean = true
		private var durationMillis: Long = 0L
		private var showProgress: Boolean = false
		private var alertColor: Int = Color.TRANSPARENT
		private var textColor: Int = Color.WHITE
		private var progressColor: Int = Color.WHITE
		private var typeface: Typeface? = null
		
		constructor(
			fragment: Fragment
		) : this(fragment.requireActivity())
		
		constructor(
			fragment: FragmentActivity
		) : this(fragment as Activity)
		
		/**
		 * Set the text in the StatusBarAlert
		 *
		 * @param text the text getting displayed in StatusBarAlert
		 * @return Builder
		 * @see CharSequence
		 */
		fun text(text: CharSequence) = apply {
			this.text = text.toString()
		}
		
		/**
		 * Set the text in the StatusBarAlert
		 *
		 * @param stringId the id of the string resource
		 * @return Builder
		 */
		fun text(@StringRes stringId: Int) = apply {
			this.text = activity.getString(stringId)
		}
		
		/**
		 * Set the text in the StatusBarAlert
		 *
		 * @param stringId the id of the string resource
		 * @param formatArgs args used to format the string
		 * @return Builder
		 */
		fun text(@StringRes stringId: Int, vararg formatArgs: Any) = apply {
			this.text = activity.getString(stringId)
		}
		
		/**
		 * Enable or disable autoHide after StatusBarAlert has been shown.
		 *
		 * @param hide whether or not to hide it automatically
		 * @return Builder
		 */
		@JvmOverloads
		fun autoHide(hide: Boolean = true) = apply {
			this.autoHide = hide
		}
		
		/**
		 * Sets custom duration before the StatusBarAlert is going to be hidden.
		 *
		 * @param millis milliseconds before hiding
		 * @return Builder
		 */
		fun duration(millis: Long) = apply {
			this.durationMillis = millis
		}
		
		/**
		 * Sets custom duration before the StatusBarAlert is going to be hidden.
		 *
		 * @param time time before hiding
		 * @param unit TimeUnit which is used to associate the time with.
		 * @return Builder
		 * @see TimeUnit
		 */
		fun duration(time: Long, unit: TimeUnit) = apply {
			this.durationMillis = TimeUnit.MILLISECONDS.convert(time, unit)
		}
		
		/**
		 * Enable or disable the ProgressBar
		 *
		 * @param show whether or not to show the ProgressBar
		 * @return Builder
		 */
		@JvmOverloads
		fun showProgress(show: Boolean = true) = apply {
			this.showProgress = show
		}
		
		/**
		 * Sets the StatusBarAlert background color
		 *
		 * @param color accepts ColorRes and ColorInt
		 * @param isColorInt helps to detect if the color is a resource or color int (not required)
		 * @return Builder
		 */
		@JvmOverloads
		fun alertColor(color: Int, isColorInt: Boolean = false) = apply {
			this.alertColor = activity.getColorSafe(color, isColorInt)
		}
		
		/**
		 * Sets the StatusBarAlert text color
		 *
		 * @param color accepts ColorRes and ColorInt
		 * @param isColorInt helps to detect if the color is a resource or color int (not required)
		 * @return Builder
		 */
		@JvmOverloads
		fun textColor(color: Int, isColorInt: Boolean = false) = apply {
			this.textColor = activity.getColorSafe(color, isColorInt)
		}
		
		/**
		 * Sets the StatusBarAlert progress bar color
		 *
		 * @param color accepts ColorRes and ColorInt
		 * @param isColorInt helps to detect if the color is a resource or color int (not required)
		 * @return Builder
		 */
		@JvmOverloads
		fun progressBarColor(color: Int, isColorInt: Boolean = false) = apply {
			this.progressColor = activity.getColorSafe(color, isColorInt)
		}
		
		/**
		 * Sets custom typeface for the text.
		 *
		 * @param typeface the custom typeface
		 * @return Builder
		 */
		fun typeface(typeface: Typeface?) = apply {
			this.typeface = typeface
		}
		
		/**
		 * Builds and return StatusBarAlert as a View
		 * @return StatusBarAlert
		 */
		fun build() = StatusBarAlert(
			activity,
			alertColor,
			text,
			textColor,
			typeface,
			showProgress,
			progressColor,
			autoHide,
			durationMillis
		)
	}
}