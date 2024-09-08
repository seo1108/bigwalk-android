package kr.co.bigwalk.app.data.walk.repository

import kr.co.bigwalk.app.data.walk.Walk
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object WalkRepository : WalkDataSource {

    override fun saveWalk(walk: Walk, saveWalkCallback: WalkDataSource.SaveWalkCallback) {
        WalkLocalDataSource.saveWalk(walk, saveWalkCallback)
    }

    override fun getAllWalks(getAllWalksCallback: WalkDataSource.GetAllWalksCallback) {
        WalkLocalDataSource.getAllWalks(getAllWalksCallback)
    }

    override fun uploadWalkData(walks: List<Walk>, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
        WalkRemoteDataSource.uploadWalkData(walks, uploadWalkDataCallback)
    }

    override fun uploadWalkData(walk: Walk, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
        WalkRemoteDataSource.uploadWalkData(walk, uploadWalkDataCallback)
    }

    override fun initializeWalkTable(walks: List<Walk>) {
        WalkLocalDataSource.initializeWalkTable(walks)
    }

}