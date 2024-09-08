package kr.co.bigwalk.app.infra.social.repository

interface InfraDataSource {

    interface DownloadSocialProfileCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun downloadSocialProfile(path: String, downloadSocialProfileCallback: DownloadSocialProfileCallback)

}