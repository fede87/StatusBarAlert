
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Status%20Bar%20Alert-green.svg?style=flat )]( https://android-arsenal.com/details/1/7037 )
[![](https://jitpack.io/v/fede87/StatusBarAlert.svg)](https://jitpack.io/#fede87/StatusBarAlert)


<img src="https://github.com/fede87/StatusBarAlert/raw/master/status_bar_alert_demo.gif" width="330" height="600" />

## What is this?

This is a small library inspired by Telegram X status bar new alert written in Kotlin.
It can show custom message with optional indeterminate progress in status bar area.
Optional autohide feature can be tweaked with custom duration.
When showing, status bar alert kindly hides status bar's icon with SYSTEM_UI_FLAG_LOW_PROFILE flag mode, if available from os.

## Supported devices

This lib is supported by every device with a SDK level of at least 14 (Android 4+. On Android 4.x the alert will be drawn below the status).

<b>Android X required for v1.1.0 and above, otherwise use old version v1.0.2.</b>

## Quick walkthrough

### Gradle

##### Step 1. Add the JitPack repository to your build file

gradle: add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
	maven { url 'https://jitpack.io' }
    }
}
```


##### Step 2. Add this line to your app's dependencies:


```gradle
implementation 'com.github.fede87:StatusBarAlert:2.0.0'
```

### Quick Sample

Create a new StatusBarAlert in activities and fragments.

Using Kotlin DSL:

```kotlin
statusBarAlert {
    autoHide()
    dduration(2, TimeUnit.SECONDS)
    showProgress()
    text("Kotlin DSL")
    alertColor(android.R.color.red)
    textColor(android.R.color.white)
    progressBarColor(android.R.color.white)
}.show()
```

Using Java way:

```kotlin
val statusBarAlertview: StatusBarAlert = StatusBarAlert.Builder(this@MainActivity)
                       .autoHide()
                       .withDuration(100)
                       .showProgress()
                       .text("autohide!")
                       .alertColor(R.color.colorPrimaryDark)
                       .textColor(R.color.colorAccent)
                       .progressBarColor(R.color.colorAccent)
                       .typeface(typeface)
                       .build().show()
                       
                       
//update status bar alert view text at any time:
statusBarAlertView.setText("UPDATED!!")
//or:
statusBarAlertView.setText(R.string.updated)
 
 
// show indeterminate progress:
statusBarAlertView.showProgress()
 
// hide indeterminate progress:
statusBarAlertView.hideProgress()
```

## License

    Copyright 2018 Federico Marin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 
