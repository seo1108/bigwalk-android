package kr.co.bigwalk.app

import android.app.Activity

interface BaseNavigator {
    fun getContext() : Activity
    fun finish()
}