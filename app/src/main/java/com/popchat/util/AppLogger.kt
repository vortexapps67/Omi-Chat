package com.popchat.util

import android.content.Context
import android.util.Log
import com.popchat.BuildConfig
import timber.log.Timber

object AppLogger {

    private const val DEFAULT_TAG = "OmiChat"

    fun init(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    fun d(message: String) {
        Timber.d(message)
    }

    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun i(message: String) {
        Timber.i(message)
    }

    fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    fun w(message: String) {
        Timber.w(message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.e(throwable, message)
        } else {
            Timber.e(message)
        }
    }

    private class DebugTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val logTag = tag ?: DEFAULT_TAG
            when (priority) {
                Log.VERBOSE -> Log.v(logTag, message)
                Log.DEBUG -> Log.d(logTag, message)
                Log.INFO -> Log.i(logTag, message)
                Log.WARN -> Log.w(logTag, message)
                Log.ERROR -> Log.e(logTag, message, t)
                else -> Log.d(logTag, message)
            }
        }
    }

    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority >= Log.ERROR) {
                // Send to crash reporting (Crashlytics, etc.)
                // Crashlytics.log(message)
                // t?.let { Crashlytics.recordException(it) }
            }
        }
    }
}