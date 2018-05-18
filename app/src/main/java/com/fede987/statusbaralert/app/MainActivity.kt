package com.fede987.statusbaralert.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fede987.statusbaralert.StatusBarAlert
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()

    }

    fun setupButtons() {

        button1.setOnClickListener({

            StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(true)
                    .showProgress(true)
                    .withText("autohide!")
                    .withAlertColor(R.color.colorPrimaryDark)
                    .build()

        })

        button2.setOnClickListener({

            StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(false)
                    .showProgress(false)
                    .withText("RED ALERT!")
                    .withAlertColor(R.color.red)
                    .build()

        })

        button3.setOnClickListener({

            StatusBarAlert.Builder(this@MainActivity)
                    .autoHide(true)
                    .withDuration(100)
                    .showProgress(false)
                    .withText("BLINK!")
                    .withAlertColor(R.color.green)
                    .build()

        })

    }
}
