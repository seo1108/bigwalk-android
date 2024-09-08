package kr.co.bigwalk.app.campaign.ranking.info

import android.app.Activity.RESULT_OK
import androidx.databinding.BaseObservable
import kr.co.bigwalk.app.BaseNavigator

class RankingInformationViewModel(private val navigator: BaseNavigator): BaseObservable() {

    fun finish() {
        navigator.getContext().setResult(RESULT_OK)
        navigator.finish()
    }
}