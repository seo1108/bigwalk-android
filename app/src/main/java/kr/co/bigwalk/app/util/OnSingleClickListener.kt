package kr.co.bigwalk.app.util

import android.view.View

abstract class OnSingleClickListener(
    private val interval: Long = 2000L
) : View.OnClickListener {
    private var clickable = true
    
    abstract fun onSingleClick(view: View)
    
    override fun onClick(view: View?) {
        if (clickable) {
            clickable = false
            view?.run {
                postDelayed({
                    clickable = true
                }, interval)
                onSingleClick(view)
            }
        }
    }
}