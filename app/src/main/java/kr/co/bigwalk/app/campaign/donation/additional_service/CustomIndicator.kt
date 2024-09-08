package kr.co.bigwalk.app.campaign.donation.additional_service

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.extension.dpToPx

class CustomIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private var customDot: MutableList<CustomDot> = mutableListOf()

    /**
     * 기본 점 생성
     * @param count 점의 갯수
     * @param position 선택된 점의 포지션
     */
    fun createDotPanel(count: Int, background: Int = R.drawable.bg_indicator_selector) {
        this.removeAllViews()
        if (count == 1) return
        repeat(count) { i ->
            customDot.add(CustomDot(context, background).apply { setPadding(0, 0, 5.dpToPx(), 0) })
            this.addView(customDot[i])
        }
        selectDot(0)
    }

    /**
     * 선택된 점 표시
     * @param position
     */
    fun selectDot(position: Int) {
        for (i in customDot.indices) {
            customDot[i].isSelected = false
            if (i == position) {
                customDot[i].isSelected = true
            }
        }
    }
}