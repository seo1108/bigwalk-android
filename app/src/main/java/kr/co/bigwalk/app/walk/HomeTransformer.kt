package kr.co.bigwalk.app.walk

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.pow

class HomeTransformer() : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val radius = height / 2f
            //90도로 기울기
            rotation = position * 90f
            //x좌표 이동, radius가 클수록 멀리 나간다
            translationX = radius * position
            //2차 함수, angle 각이 작을수록 평평하게 이동한다.
            val angle = 0.0012f
            translationY = angle * (translationX).pow(2)
        }
    }
}