package kr.co.bigwalk.app.extension

import android.view.MotionEvent
import android.view.View

fun View.inputViewTouchEvent(event: MotionEvent): Boolean {
    if (this.hasFocus()) {
        this.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_SCROLL -> {
                this.parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
    }
    return false
}