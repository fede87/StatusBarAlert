package com.fede987.statusbaralert.app

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fede987.statusbaralert.StatusBarAlert
import com.fede987.statusbaralert.StatusBarAlertView
import kotlinx.android.synthetic.main.activity_main.button1
import kotlinx.android.synthetic.main.activity_main.button2
import kotlinx.android.synthetic.main.activity_main.button3
import kotlinx.android.synthetic.main.activity_main.button4
import kotlinx.android.synthetic.main.activity_main.dark_status_checkbox

class MainActivity : AppCompatActivity() {

    var typeface: Typeface? = null
    val handler: Handler = Handler()
    var alert1: StatusBarAlertView? = null
    var alert2: StatusBarAlertView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createCustomTypeface()

        setupButtons()
    }

    override fun onDestroy() {
        alert1 = null
        alert2 = null
        handler.removeCallbacksAndMessages(null)
        StatusBarAlert.hide(this) {
            Toast.makeText(applicationContext, "hidden alert @onDestroy", Toast.LENGTH_SHORT).show()
        }
        super.onDestroy()
    }

    private fun createCustomTypeface() {
        typeface = Typeface.createFromAsset(assets, "font/Lato-Regular.ttf")
    }

    private fun setupButtons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dark_status_checkbox.setOnCheckedChangeListener { button, checked ->
                if (checked) window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        } else dark_status_checkbox.visibility = View.GONE

        button1.setOnClickListener {

            alert1 = StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(true)
                    .showProgress(true)
                    .withDuration(10000)
                    .withText("autohide!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.colorAccent)
                    .withIndeterminateProgressBarColor(R.color.colorPrimary)
                    .withTextColor(R.color.colorPrimary)
                    .build()

            handler.postDelayed({
                alert1?.updateText("Phase 1!")
                alert1?.showIndeterminateProgress()
            }, 2000)


            handler.postDelayed({
                alert1?.updateText("Phase 2!")
                alert1?.showIndeterminateProgress()
            }, 4000)

            handler.postDelayed({
                alert1?.updateText("Completed!")
                alert1?.hideIndeterminateProgress()
            }, 7500)

        }

        button2.setOnClickListener {

            alert2 = StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(false)
                    .showProgress(false)
                    .withText("RED ALERT!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.red)
                    .withTextColor(R.color.colorPrimaryDark)
                    .withIndeterminateProgressBarColor(R.color.colorPrimaryDark)
                    .build()

            handler.postDelayed({
                if (alert2?.parent != null)
                    alert2?.updateText("INFO UPDATED!!")
            }, 2000)

        }

        button3.setOnClickListener {

            StatusBarAlert.Builder(
                    this@MainActivity)
                    .autoHide(true)
                    .withDuration(400)
                    .showProgress(false)
                    .withText("BLINK!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.green)
                    .withTextColor(R.color.colorAccent)
                    .withIndeterminateProgressBarColor(R.color.colorAccent)
                    .build()

        }

        button4.setOnClickListener {

            StatusBarAlert.Builder(
                    this@MainActivity)
                    .autoHide(true)
                    .withDuration(2000)
                    .showProgress(false)
                    .withText("transparent alert!")
                    .withAlertColor(android.R.color.transparent)
                    .withTextColor(R.color.colorAccent)
                    .withIndeterminateProgressBarColor(R.color.colorAccent)
                    .withTypeface(typeface!!)
                    .build()
        }
    }
}
