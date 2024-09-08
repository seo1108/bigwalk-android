package kr.co.bigwalk.app.util

import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import java.util.*

object MainRandomViewMaker {

    fun getBackgroundResourceByTime(): Int {
        val now = Calendar.getInstance()
        return when (now.get(Calendar.MONTH) + 1) {
            in 3..5 -> {
                when (now.get(Calendar.HOUR_OF_DAY)) {
                    in 6..17 -> {
                        R.drawable.spring_day
                    }
                    else -> {
                        R.drawable.spring_night
                    }
                }
            }
            in 6..8 -> {
                when (now.get(Calendar.HOUR_OF_DAY)) {
                    in 6..17 -> {
                        R.drawable.summer_day
                    }
                    else -> {
                        R.drawable.summer_night
                    }
                }
            }
            in 9..11 -> {
                when (now.get(Calendar.HOUR_OF_DAY)) {
                    in 6..17 -> {
                        R.drawable.autumn_day
                    }
                    else -> {
                        R.drawable.autumn_night
                    }
                }
            }
            else -> {
                when (now.get(Calendar.HOUR_OF_DAY)) {
                    in 6..17 -> {
                        R.drawable.winter_day
                    }
                    else -> {
                        R.drawable.winter_night
                    }
                }
            }
        }
    }

    fun getRandomComment(): String {
        val comment = getStringResourceByTime() ?: return ""
        return comment[Random().nextInt(comment.size)]
    }

    private fun getStringResourceByTime(): Array<String>? {
        val now = Calendar.getInstance()
        return when (now.get(Calendar.HOUR_OF_DAY)) {
            in 0..5 -> {
                BigwalkApplication.context?.resources?.getStringArray(R.array.main_dawn)
            }
            in 6..11 -> {
                BigwalkApplication.context?.resources?.getStringArray(R.array.main_morning)
            }
            in 12..17 -> {
                BigwalkApplication.context?.resources?.getStringArray(R.array.main_after_noon)
            }
            else -> {
                BigwalkApplication.context?.resources?.getStringArray(R.array.main_evening)
            }
        }
    }

    fun getCrewCampaignPreviewBackground(): Int {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                R.drawable.aos_bg_crew_preview_day_bg
            }
            else -> {
                R.drawable.aos_bg_crew_preview_night_bg
            }
        }
    }
}