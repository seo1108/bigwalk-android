package kr.co.bigwalk.app.infra.social.repository

import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.util.SingleThreadExecutor
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.tempDownloadedFilePath
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object InfraRepository : InfraDataSource {
    override fun downloadSocialProfile(path: String, downloadSocialProfileCallback: InfraDataSource.DownloadSocialProfileCallback) {
        RemoteApiManager.getService().downloadSocialProfile(path).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                downloadSocialProfileCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            val responseBody = it
                            SingleThreadExecutor().execute {
                                if (toFile(responseBody)) downloadSocialProfileCallback.onSuccess()
                                else downloadSocialProfileCallback.onFailed("social profile download fail!!")
                            }
                        }
                    }
                    else -> downloadSocialProfileCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    @Throws(IOException::class)
    private fun toFile(responseBody: ResponseBody): Boolean {
        BigwalkApplication.context?.let {
            val file = File(tempDownloadedFilePath)
            val fileReader = ByteArray(4096)
            val fileSize = responseBody.contentLength()
            var fileSizeDownloaded = 0
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) break
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read
                DebugLog.d("download file size = $fileSizeDownloaded, original file size = $fileSize")
            }

            outputStream.flush()
            inputStream.close()
            outputStream.close()
            return true
        }
        return false
    }
}