package com.fede987.statusbaralert.app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fede987.statusbaralert.StatusBarAlert
import com.fede987.statusbaralert.utils.statusBarAlert
import kotlinx.android.synthetic.main.activity_main.button1
import kotlinx.android.synthetic.main.activity_main.button2
import kotlinx.android.synthetic.main.activity_main.button3
import kotlinx.android.synthetic.main.activity_main.button4
import kotlinx.android.synthetic.main.activity_main.dark_status_checkbox
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var typeface: Typeface? = null
    val handler: Handler = Handler(Looper.getMainLooper())
    var alert1: StatusBarAlert? = null
    var alert2: StatusBarAlert? = null

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
                    .duration(10000)
                    .text("autohide!")
                    .typeface(typeface)
                    .alertColor(R.color.colorAccent)
                    .progressBarColor(R.color.colorPrimary)
                    .textColor(R.color.colorPrimary)
                    .build().apply { show() }

            handler.postDelayed({
                alert1?.setText("Phase 1!")
                alert1?.showProgress()
            }, 2000)


            handler.postDelayed({
                alert1?.setText("Phase 2!")
                alert1?.showProgress()
            }, 4000)

            handler.postDelayed({
                alert1?.setText("Completed!")
                alert1?.hideProgress()
            }, 7500)

        }

        button2.setOnClickListener {
            alert2 = statusBarAlert {
                autoHide(false)
                showProgress(false)
                text("RED ALERT!")
                typeface(typeface)
                alertColor(R.color.red)
                textColor(R.color.colorPrimaryDark)
            }.show()

            handler.postDelayed({
                if (alert2?.parent != null)
                    alert2?.setText("INFO UPDATED!!")
            }, 2000)

        }

        button3.setOnClickListener {
            statusBarAlert {
                autoHide()
                duration(400)
                showProgress(false)
                text("BLINK!")
                typeface(typeface)
                alertColor(R.color.green)
                textColor(R.color.colorAccent)
                progressBarColor(R.color.colorAccent)
            }.show()
        }

        button4.setOnClickListener {
            statusBarAlert {
                autoHide()
                duration(2, TimeUnit.SECONDS)
                showProgress(false)
                text("transparent alert!")
                alertColor(Color.TRANSPARENT)
                textColor(R.color.colorAccent)
                progressBarColor(R.color.colorAccent)
                typeface(typeface)
            }.show()
        }
    }
}
