package kr.co.bigwalk.app.data.walk

import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.walk.repository.WalkDataSource
import kr.co.bigwalk.app.data.walk.repository.WalkRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.dayDiff
import kr.co.bigwalk.app.util.getTodayDate
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class WalkUtil {
    companion object {
        fun syncDailyWalk(): Int { // CurrentDay
            val now = Calendar.getInstance()
            val nowDay = now.get(Calendar.DAY_OF_MONTH)
            val currentDay = PreferenceManager.getCurrentDay()




            DebugLog.d("syncDailyWalk() onSensorChanged " + getTodayDate() + " " + nowDay + " " + currentDay)
            //if(nowDay != currentDay){
            if (currentDay < 100) {
                PreferenceManager.saveCurrentDay(getTodayDate().toInt())
                WalkRepository.saveWalk(getYesterdayWalk(PreferenceManager.getDailyStep()), object : WalkDataSource.SaveWalkCallback {
                    override fun onSuccess(id: Long) {
                    }
                })
            } else if (getTodayDate().toInt() != currentDay) {
                DebugLog.d("syncDailyWalk() onSensorChanged " + currentDay + " " + dayDiff(currentDay.toString(), getTodayDate()) + " " + PreferenceManager.getDailyStep())
                DebugLog.d("day has passed")
                //PreferenceManager.saveCurrentDay(nowDay)
                /*WalkRepository.saveWalk(getYesterdayWalk(PreferenceManager.getDailyStep()), object : WalkDataSource.SaveWalkCallback {
                    override fun onSuccess(id: Long) {
                    }
                })*/

                // 오늘날짜와 마지막 저장된 날짜의 차이일을 계산해서, dailyStep의 하루평균을 구하고 서버에 전송함
                var dayDiff = dayDiff(currentDay.toString(), getTodayDate())
                if (PreferenceManager.getDailyStep() > 0) {
                    WalkRepository.saveWalk(
                        getYesterdayWalk(PreferenceManager.getDailyStep()/dayDiff.toInt()),
                        object : WalkDataSource.SaveWalkCallback {
                            override fun onSuccess(id: Long) {
                            }
                        })
                } else {
                    WalkRepository.saveWalk(getYesterdayWalk(PreferenceManager.getDailyStep()), object : WalkDataSource.SaveWalkCallback {
                        override fun onSuccess(id: Long) {
                        }
                    })
                }
                PreferenceManager.saveCurrentDay(getTodayDate().toInt())
                PreferenceManager.saveDailyStep(0)
            }
            return PreferenceManager.getCurrentDay()
        }

        fun getCurrentWalk(step: Int): Walk {
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR_OF_DAY, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            val walk = Walk()
            walk.startTime = now.time
            walk.endTime = DateUtils.addHours(walk.startTime, 1)
            walk.steps = step
            return walk
        }

        fun getYesterdayWalk(step: Int): Walk {
            val now = Calendar.getInstance()
            now.add(Calendar.DATE, -1)
            now.set(Calendar.HOUR_OF_DAY, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            val walk = Walk()
            walk.startTime = now.time
            walk.endTime = DateUtils.addHours(walk.startTime, 1)
            walk.steps = step
            return walk
        }
    }
}