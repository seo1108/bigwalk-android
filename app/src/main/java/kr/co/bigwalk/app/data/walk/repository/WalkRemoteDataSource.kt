package kr.co.bigwalk.app.data.walk.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.dto.CurrentWalk
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.sensor.WalkSensor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object WalkRemoteDataSource : WalkDataSource {
    override fun saveWalk(walk: Walk, saveWalkCallback: WalkDataSource.SaveWalkCallback) {}
    override fun getAllWalks(getAllWalksCallback: WalkDataSource.GetAllWalksCallback) {}
    override fun initializeWalkTable(walks: List<Walk>) {}

    override fun uploadWalkData(walks: List<Walk>, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
        RemoteApiManager.getService().uploadWalks(walks).enqueue(object : Callback<CurrentWalk> {
            override fun onFailure(call: Call<CurrentWalk>, t: Throwable) {
                uploadWalkDataCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<CurrentWalk>, response: Response<CurrentWalk>) {
                if (response.code() == 200) {
                    uploadWalkDataCallback.onSuccess(response.body()!!)
                } else {
                    uploadWalkDataCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun uploadWalkData(walk: Walk, uploadWalkDataCallback: WalkDataSource.UploadWalkDataCallback) {
        RemoteApiManager.getService().uploadWalk(walk).enqueue(object : Callback<CurrentWalk> {
            override fun onFailure(call: Call<CurrentWalk>, t: Throwable) {
                uploadWalkDataCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<CurrentWalk>, response: Response<CurrentWalk>) {
                if (response.code() == 200) {
                    uploadWalkDataCallback.onSuccess(response.body()!!)
                } else {
                    uploadWalkDataCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

}