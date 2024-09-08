package kr.co.bigwalk.app.sign_in.agreement

import kr.co.bigwalk.app.BaseNavigator

interface AgreementNavigator: BaseNavigator {

    fun moveToNext(agreeWithMarketing: Boolean)

}