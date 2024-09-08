package kr.co.bigwalk.app.story.detail

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.databinding.BottomSheetStoryDetailBinding

class StoryDetailFragment(private val campaignId: Long, private val donatedSteps: Long = 0) : BottomSheetDialogFragment() {

    private lateinit var behavior : BottomSheetBehavior<FrameLayout>
    private lateinit var viewModel: StoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            val bundle = Bundle()
            bundle.putLong("campaign_id", campaignId)
            FirebaseAnalytics.getInstance(it).logEvent("story_detail", bundle)
        }
        viewModel = StoryDetailViewModel(this, campaignId, donatedSteps)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: BottomSheetStoryDetailBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_story_detail, container,false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { bottomSheetDialog: DialogInterface ->
            bottomSheetDialog as BottomSheetDialog
            val frameLayout = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(frameLayout!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
                }

                override fun onSlide(view: View, v: Float) {}
            })
            viewModel.bottomSheetBehavior = behavior
        }
        return dialog
    }

}