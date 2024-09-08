package kr.co.bigwalk.app.data.walk.repository

import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.dto.CurrentWalk
import kr.co.bigwalk.app.data.report.dto.UserStatisticsResponse

interface WalkDataSource {

    interface SaveWalkCallback {
        fun onSuccess(id: Long)
    }

    interface GetAllWalksCallback {
        fun onSuccess(walks: List<Walk>)
        fun onEmpty()
    }

    interface UploadWalkDataCallback {
        fun onSuccess(currentWalk: CurrentWalk)
        fun onFailed(reason: String)
    }

    fun saveWalk(walk: Walk, saveWalkCallback: SaveWalkCallback)

    fun getAllWalks(getAllWalksCallback: GetAllWalksCallback)

    fun uploadWalkData(walks: List<Walk>, uploadWalkDataCallback: UploadWalkDataCallback)

    fun uploadWalkData(walk: Walk, uploadWalkDataCallback: UploadWalkDataCallback)

    fun initializeWalkTable(walks: List<Walk>)

}