package kr.co.bigwalk.app.lock_screen

import kr.co.bigwalk.app.BaseNavigator

interface LockScreenNavigator: BaseNavigator {
    fun setProgressPercentage(percentage : Int)
}