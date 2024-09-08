package kr.co.bigwalk.app.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun gaSendLogEvent(context: Context, eventName: String) {
    FirebaseAnalytics.getInstance(context).logEvent(eventName, Bundle())
}