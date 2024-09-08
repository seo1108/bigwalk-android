package kr.co.bigwalk.app.feed

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kr.co.bigwalk.app.ext.multiImagePicker.builder.MultiImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.donation.DonationData
import kr.co.bigwalk.app.campaign.donation.DonationStepMissionViewModel
import kr.co.bigwalk.app.databinding.BottomSheetSelectPhotoBinding
import kr.co.bigwalk.app.feed_guide.FeedGuideActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.isPermissionGranted
import kr.co.bigwalk.app.util.requestPermission

class FeedPhotoSelectFragment: BottomSheetDialogFragment() {

    companion object {

        const val CAMPAIGN_ID = "CAMPAIGN_ID"

        fun newInstance(campaignId: Long): FeedPhotoSelectFragment{
            val fragment = FeedPhotoSelectFragment()
            val bundle = Bundle()
            bundle.putLong(CAMPAIGN_ID, campaignId)
            fragment.arguments = bundle
            return fragment
        }

        var campaignId = -1L
    }
    private lateinit var behavior : BottomSheetBehavior<FrameLayout>
    private var viewModel: DonationStepMissionViewModel? = null
    private var mCampaignId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            mCampaignId = arguments?.getLong(CAMPAIGN_ID, -1L)?: -1L
            if (mCampaignId != -1L) {
                viewModel = DonationStepMissionViewModel(it, DonationData(mCampaignId))
            }

            campaignId = mCampaignId
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomSheetSelectPhotoBinding.inflate(layoutInflater)
        binding.btnTakePhoto.setOnClickListener {
            viewModel?.donateByMission()
            viewModel?.dismiss()
            showMissionGuide()
            FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_btn_photo_shoot_click", Bundle())
        }
        binding.btnLoadPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 안드로이드13 인 경우
                if (isPermissionGranted(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    requestPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    viewModel?.dismiss()

                    MultiImagePicker.with(this) // `this` refers to activity or fragment
                        .setSelectionLimit(3)  // The number of max image selection you want from user at a time, MAX is 30
                        .open()

                }
            } else {
                if (isPermissionGranted(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                    requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    viewModel?.dismiss()
                    TedImagePicker.with(requireActivity()).startMultiImage { uriList ->
                        val list = uriList.map { it.toString() }
                        val arraylist = arrayListOf<String>()
                        arraylist.addAll(list)
                        viewModel?.donateByMission(arraylist)
                    }
                }
            }

            showMissionGuide()
            FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_btn_photo_load_click", Bundle())
        }
        binding.btnMissionGuide.setOnClickListener {
            showMissionGuide()
            viewModel?.dismiss()
        }
        return binding.root
    }

    private fun showMissionGuide() {
        startActivity(FeedGuideActivity.getIntent(requireContext(), mCampaignId))
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { bottomSheetDialog: DialogInterface ->
            bottomSheetDialog as BottomSheetDialog
            val frameLayout = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(frameLayout!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) dismissAllowingStateLoss()
                }

                override fun onSlide(view: View, v: Float) {}
            })
            viewModel?.bottomSheetBehavior = behavior
        }
        return dialog
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DebugLog.d("____________________________________________________________")
        if (requestCode == MultiImagePicker.REQUEST_PICK_MULTI_IMAGES && resultCode == RESULT_OK) {
            val result = MultiImagePicker.Result(data)
            if (result.isSuccess()) {
                val imageListInUri = result.getImageList() // List os selected images as content Uri format
                //You can also request list as absolute filepath instead of Uri as below
                val imageListInAbsFilePath = result.getImageListAsAbsolutePath(requireContext())
                //do your stuff from the selected images list received
                val imageList = arrayListOf<String>();
                imageListInUri.forEach {
                    imageList.add(it.toString())
                }
                viewModel?.donateByMission(imageList)
            }
        }
    }*/

}