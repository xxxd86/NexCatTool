package com.nexify.layoutspeclibrary

import CatOverlayLayout
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import java.lang.ref.WeakReference

class NexApplication : Application(){
    companion object {
        @SuppressLint("StaticFieldLeak")
        var activityWeakReference: WeakReference<Activity>? = null

        var catOverlayLayout:CatOverlayLayout? = null

    }

    override fun onCreate() {
        super.onCreate()
        // 注册 Activity 生命周期回调
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 不需要处理
                catOverlayLayout = CatOverlayLayout(activity.baseContext, null)
            }

            override fun onActivityStarted(activity: Activity) {
                // 不需要处理
            }

            override fun onActivityResumed(activity: Activity) {
                // 当活动被恢复时，更新当前 Activity
                activityWeakReference = WeakReference(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                val intent = Intent("com.example.REMOVE_OVERLAY")
                sendBroadcast(intent)
            }

            override fun onActivityStopped(activity: Activity) {
                // 不需要处理
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                // 不需要处理
            }

            override fun onActivityDestroyed(activity: Activity) {
                // 不需要处理
                activityWeakReference?.clear()
            }
        })
    }
}