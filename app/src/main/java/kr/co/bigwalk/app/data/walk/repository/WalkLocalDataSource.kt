package kr.co.bigwalk.app.data.walk.repository

import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.AppDatabase
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.SingleThreadExecutor

object WalkLocalDataSource : WalkDataSource {

    override fun saveWalk(walk: Walk, saveWalkCallback: WalkDataSource.SaveWalkCallback) {
        SingleThreadExecutor().execute {
            val id = AppDatabase(BigwalkApplication.context!!).walkDao().insert(walk)
            saveWalkCallback.onSuccess(id)
        }
    }

    override fun getAllWalks(getAllWalksCallback: WalkDataSource.GetAllWalksCallback) {
        SingleThreadExecutor().execute {
            val walks = AppDatabase(BigwalkApplication.context!!).walkDao().findAll()
            if (walks.isNullOrEmpty()) getAllWalksCallback.onEmpty()
            getAllWalksCallback.onSuccess(walks)
        }
    }

    override fun uploadWalkData(walks: List<Walk>, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
    }

    override fun uploadWalkData(walk: Walk, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
    }

    override fun initializeWalkTable(walks: List<Walk>) {
        SingleThreadExecutor().execute {
            AppDatabase(BigwalkApplication.context!!).walkDao().deleteAll(walks)
        }
    }

}