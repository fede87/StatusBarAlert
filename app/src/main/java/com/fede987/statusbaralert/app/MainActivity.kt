package com.fede987.statusbaralert.app

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.fede987.statusbaralert.StatusBarAlert
import com.fede987.statusbaralert.StatusBarAlertView
import kotlinx.android.synthetic.main.activity_main.*

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
        StatusBarAlert.hide(this, Runnable{})
        super.onDestroy()
    }

    private fun createCustomTypeface() {
        typeface = Typeface.createFromAsset(assets, "font/Lato-Regular.ttf")
    }


    private fun setupButtons() {

        button1.setOnClickListener {

            alert1 = StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(true)
                    .showProgress(true)
                    .withDuration(10000)
                    .withText("autohide!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.colorAccent)
                    .build()

            handler.postDelayed({
                alert1?.updateText("Phase 1!")
                alert1?.showIndeterminateProgress()
            },2000)


            handler.postDelayed({
                alert1?.updateText("Phase 2!")
                alert1?.showIndeterminateProgress()
            },4000)

            handler.postDelayed({
                alert1?.updateText("Completed!")
                alert1?.hideIndeterminateProgress()
            },7500)

        }

        button2.setOnClickListener {

            alert2 = StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(false)
                    .showProgress(false)
                    .withText("RED ALERT!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.red)
                    .build()

            handler.postDelayed({
                if(alert2?.parent!=null)
                    alert2?.updateText("INFO UPDATED!!")
            },2000)


        }

        button3.setOnClickListener {

            StatusBarAlert.Builder(
                    this@MainActivity)
                    .autoHide(true)
                    .withDuration(100)
                    .showProgress(false)
                    .withText("BLINK!")
                    .withTypeface(typeface!!)
                    .withAlertColor(R.color.green)
                    .build()

        }
    }
}
