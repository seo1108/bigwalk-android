package kr.co.bigwalk.app.campaign.donation.additional_service

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kr.co.bigwalk.app.R

class CustomDot @JvmOverloads constructor(
    context: Context,
    background: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    init {
        init(background, attrs)
    }

    private fun init(background: Int, attrs: AttributeSet?) {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = li.inflate(R.layout.layout_custom_dot, this, false)
        v.setBackgroundResource(background)
        addView(v)
    }

}