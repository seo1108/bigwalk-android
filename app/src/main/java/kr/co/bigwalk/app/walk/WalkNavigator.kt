package kr.co.bigwalk.app.walk

import kr.co.bigwalk.app.BaseNavigator

interface WalkNavigator : BaseNavigator {

    fun setProgressPercentage(percentage : Int)
}