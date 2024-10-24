package kr.co.bigwalk.app.walk.sensor

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.event.alarm.EventManager
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.alarm.StepManager

//센서에 대한 부분만 담당하는 객체
object WalkSensor : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var currentStepCount = 0

    var registeredListener: SensorEventListener? = null
    var isAlreadyRegistered = false
    var stepCallback: StepCallback? = null
    var stepMaxCallback: StepMaxCallback? = null

    init {
        val sensorService = BigwalkApplication.context?.getSystemService(Context.SENSOR_SERVICE)
        if (sensorService is SensorManager) this.sensorManager = sensorService
    }

    fun registerStepSensor() {
        registerStepCounterSensor()
    }

    fun unregisterStepSensor() {

    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            DebugLog.d("센서 변화 감지 onSensorChanged TYPE_STEP_COUNTER ${event.values?.get(0)!!.toInt()} ${PreferenceManager.getDailyStep()} ${PreferenceManager.getDonableStep()} ${PreferenceManager.getRecentStep()} ${System.currentTimeMillis()}")
            currentStepCount = event.values?.get(0)!!.toInt()
            try {
                PreferenceManager.saveSensorStep(event.values?.get(0)!!.toInt())
            } catch (_: Exception) {}
            // 기기 재부팅등의 이유로 누적 걸음수가 0이면 recentStep도 0으로 세팅
            if (currentStepCount == 0) {
                PreferenceManager.saveRecentStep(0)
            }

            DebugLog.d("currentStep:$currentStepCount")
        } else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            DebugLog.d("센서 변화 감지 onSensorChanged TYPE_ACCELEROMETER")
            event.values?.clone()?.let {
                val step = AccelerometerStepCalculator.getInstance()
                    .calculateStep(event.values.clone())
                try {
                    PreferenceManager.saveSensorStep(step)
                } catch (_: Exception) {}
                if (step > 0) {
                    currentStepCount += step
                }
            }
        }

        if (PreferenceManager.getRecentStep() == 0)
            PreferenceManager.saveRecentStep(currentStepCount)



        /** 2023-06-01
         * Preference 에 저장된 마지막 센서 감지 시간과 현재 시간의 차이가 24시간 이상이면,
         * dailyStep을 0으로 세팅
         * dailyStep을 0으로 세팅하는 이유는 sleep 시간동안 축적된 걸음수를 오늘의 걸음수로 반영하면 금일 걸음수가 과도하게 잡히기 때문
         * donableStep은 PreferenceManager.getDonableStep() + currentStepCount - PreferenceManager.getRecentStep() (현행유지)
         * donableStep의 경우는 백그라운드 슬립시간에 걸은 걸음수도 적립해야 함
         */
        // 이벤트 스텝은 걸음수 유실 안되게해야함
        EventManager.calculateStep(currentStepCount - PreferenceManager.getRecentStep())

        // 최근 시간을 저장 (timestamp)
        PreferenceManager.saveRecentStepTime(System.currentTimeMillis())
        // 하루 이상 차이가 날때 오늘의 걸음수는 0으로 세팅
        // 최근 걸음수는 currentStepCount로 세팅
        if (PreferenceManager.getRecentStepTime() > 0
            && System.currentTimeMillis() - PreferenceManager.getRecentStepTime() > 86400000) {

            PreferenceManager.saveRecentStep(currentStepCount)
            PreferenceManager.saveDailyStep(0)
        }

        StepManager.calculateStep(currentStepCount - PreferenceManager.getRecentStep(), stepCallback)

        PreferenceManager.saveRecentStep(currentStepCount)
    }

    private fun isSupportStepCounter(): Boolean {
        val packageManager: PackageManager? = BigwalkApplication.context?.packageManager
        packageManager?.let {
            return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
        }
        return false
    }

    private fun registerStepCounterSensor() {
        this.stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.stepSensor?.let {
            this.sensorManager?.registerListener(
                this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL
            )
            isAlreadyRegistered = true
            registeredListener = this
            Log.d("WalkSensor", "register StepCounter")
        }
    }

}