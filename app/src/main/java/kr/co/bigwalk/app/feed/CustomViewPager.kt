package kr.co.bigwalk.app.feed

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): ViewPager(context, attrs) {

    private var canScroll = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onInterceptTouchEvent(ev)
    }

    fun setSwipeEnabled(enabled: Boolean) {
        this.canScroll = enabled
    }
}