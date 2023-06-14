package com.shubh.learn

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import com.shubh.learn.utills.CrashReportingTree
import com.shubh.learn.utills.SharedPref
import timber.log.Timber
import java.util.*

class SebateksApp : Application() {
    var manager: SharedPref? = null
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    val DISK_CACHE_SIZE = 10 * 1024 * 1024 // 10 MB

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
        manager = SharedPref(applicationContext)
        context = applicationContext
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //to disable dark mode
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        fun showToast(mContext: Context?, msg: String) { Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show() }
        fun get(): SebateksApp? { return get() }

    }
}
