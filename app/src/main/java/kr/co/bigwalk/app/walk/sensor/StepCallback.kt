package kr.co.bigwalk.app.walk.sensor

interface StepCallback {
    fun onStepChanged(dailyStep: Int, donableStep: Int)
}