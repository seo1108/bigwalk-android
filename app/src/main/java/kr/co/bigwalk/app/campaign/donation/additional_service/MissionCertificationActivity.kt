package kr.co.bigwalk.app.campaign.donation.additional_service

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityMissionCertificationBinding
import kr.co.bigwalk.app.extension.uriToBitmap
import kr.co.bigwalk.app.util.CameraUtil
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.screenshotBitmapToFile
import kr.co.bigwalk.app.util.takeScreenShot
import java.io.File

class MissionCertificationActivity : AppCompatActivity() {

    companion object {
        const val PARAM_ACTION_MISSION = "PARAM_ACTION_MISSION"
        const val PARAM_IMAGE_URIS = "PARAM_IMAGE_URIS"
        const val PARAM_FROM_CAMPAIGN = "PARAM_FROM_CAMPAIGN"
    }

    private lateinit var binding: ActivityMissionCertificationBinding
    private lateinit var viewModel: MissionCertificationViewModel

    private val cameraUtil = CameraUtil(this)

    private var mAdapter: MissionCertificationAdapter? = null

    private val viewpagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.indicatorMissionCertification.selectDot(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mission_certification)
        binding.lifecycleOwner = this

        FirebaseAnalytics.getInstance(this).logEvent("feed_upload_view", Bundle())

        val data = intent.getStringExtra(PARAM_ACTION_MISSION)
        val fromCampaign = intent.getBooleanExtra(PARAM_FROM_CAMPAIGN, false)

        var uriList = emptyList<Uri>()
        if (intent.hasExtra(PARAM_IMAGE_URIS)) {
            val uris = intent.getStringArrayListExtra(PARAM_IMAGE_URIS)

            if (uris != null) {
                if (uris.size > 3) showAlert()
                uriList = uris.toList().map { it.toUri() }.take(3)
            }
        }

        val missionDonationData = Gson().fromJson(data, MissionDonationData::class.java)
        missionDonationData?.let {
            viewModel = MissionCertificationViewModel(this, cameraUtil, missionDonationData, uriList, fromCampaign)
            binding.viewModel = viewModel
        }
        mAdapter = MissionCertificationAdapter()
        binding.viewpagerMissionCertification.adapter = mAdapter
        binding.viewpagerMissionCertification.registerOnPageChangeCallback(viewpagerCallback)
        setDataViewPager(uriList, missionDonationData)

        binding.agreementFeed.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.viewModel?.setCheckedState(isChecked)
        }

        setToolbar()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_exit)
            title = "챌린지 참여 인증"
        }
    }

    private fun bindViewModel() {
        with(viewModel) {
            isCameraMode.observe(this@MissionCertificationActivity, Observer {
                invalidateOptionsMenu()
            })
        }
    }



    fun showAlert() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.alert_selected_max_noti))
            .setPositiveButton("확인") { _, _ -> }
        dialogBuilder.show()
    }

    fun getImagesFile(uris: List<Uri>): List<File> {

        val files = mutableListOf<File>()

        repeat(uris.size) { idx ->
            if (idx == 0) {
                val view = (binding.viewpagerMissionCertification[0] as RecyclerView)
                    .findViewHolderForAdapterPosition(0)?.itemView
                view?.let {
                    val bitmap = takeScreenShot(it)
                    val file = screenshotBitmapToFile(bitmap)
                    files.add(file)
                }
            }else {
                try {
                    val bitmap = uris[idx].uriToBitmap(this)
                    binding.ivEmpty.setImageBitmap(bitmap)
                    val capture = takeScreenShot(binding.ivEmpty)
                    val file = screenshotBitmapToFile(capture)
                    files.add(file)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return files
    }

    fun setDataViewPager(uri: List<Uri>, missionData: MissionDonationData) {
        mAdapter?.setItems(uri, missionData)
        binding.indicatorMissionCertification.createDotPanel(uri.size)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraUtil.shutdown()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        cameraUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.feed_certification_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isCameraMode: Boolean = viewModel.isCameraMode.value ?: true
        menu.findItem(R.id.share).isVisible = !isCameraMode
        menu.findItem(R.id.retry).isVisible = !isCameraMode

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.export -> {
                viewModel.share()
                FirebaseAnalytics.getInstance(this).logEvent("feed_upload_btn_share_click", Bundle())
            }
            R.id.save_image -> {
                viewModel.galleryAddFile()
                FirebaseAnalytics.getInstance(this).logEvent("feed_upload_btn_download_click", Bundle())
            }
            R.id.retry -> {
                viewModel.retryCamera()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}