<img src="https://github.com/fede87/StatusBarAlert/raw/master/status_bar_alert_demo.gif" width="330" height="600" />

## What is this?

This is a small library inspired by Telegram X status bar new alert written in Kotlin.
It can show custom message with optional indeterminate progress in status bar area.
Optional autohide feature can be tweaked with custom duration.
When showing, status bar alert kindly hides status bar's icon with SYSTEM_UI_FLAG_LOW_PROFILE flag mode, if avaible from os.

## Supported devices

This lib is supported by every device with a SDK level of at least 14 (Android 4+).


## Quick walkthrough

### Gradle

Add this line to your app's dependecies:

implementation 'com.fede987:status-bar-alert:1.0.0'

### Quick Sample

Show a new status bar alert

```
StatusBarAlert.Builder(this@MainActivity)
                       .autoHide(true)
                       .withDuration(100)
                       .showProgress(true)
                       .withText("autohide!")
                       .withAlertColor(R.color.colorPrimaryDark)
                       .build()
```

Hide all status bar alerts for current activity with a "on hidden" callback:

```
StatusBarAlert.hide(this@MainActivity, Runnable {})
```





 
