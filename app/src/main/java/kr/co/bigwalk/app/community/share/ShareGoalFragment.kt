package kr.co.bigwalk.app.community.share

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupShareResponse
import kr.co.bigwalk.app.databinding.FragmentShareGoalBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.extension.setMargin
import kr.co.bigwalk.app.util.takeScreenShot
import java.io.ByteArrayOutputStream


class ShareGoalFragment : Fragment() {
    private lateinit var binding: FragmentShareGoalBinding
    private val groupShareViewModel: GroupShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GroupShareViewModel::class.java)
    }
    private var shareItem: GroupShareResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_goal, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = groupShareViewModel
            shareModeTab.run {
                addTab(shareModeTab.newTab().setIcon(R.drawable.icon_27_step_share))
                addTab(shareModeTab.newTab().setIcon(R.drawable.icon_24_carbon_share))
                addTab(shareModeTab.newTab().setIcon(R.drawable.icon_24_planting_share))
                setMargin(7.dpToPx(), 0, 7.dpToPx(), 0)

                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.position ?: return
                        shareItem?.let { share ->
                            when (tab.position) {
                                0 -> {
                                    shareGroupNameLabel.text =
                                        String.format(getString(R.string.share_group_goal_step), share.getMaxName(), share.report.donatedSteps())
                                }
                                1 -> {
                                    shareGroupNameLabel.text =
                                        String.format(getString(R.string.share_group_goal_carbon), share.getMaxName(), share.report.getReducedCarbonAmount())
                                }
                                2 -> {
                                    shareGroupNameLabel.text =
                                        String.format(getString(R.string.share_group_goal_planting), share.getMaxName(), share.report.getPlantingAmount())
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }
                })
            }
            nextPage.setOnClickListener {
                startActivityForResult(
                    ShareEditStickerActivity.getIntent(requireContext(), viewToByteArray(imageContainer), groupShareViewModel.groupId),
                    ShareEditStickerActivity.KEY_SHARE
                )
            }
        }
    }

    private fun viewToByteArray(view: View): ByteArray {
        val bitmap = takeScreenShot(view)
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)

        return stream.toByteArray()
    }

    private fun bindViewModel() {
        with(groupShareViewModel) {
            shareContents.observe(viewLifecycleOwner, Observer { contents ->
                shareItem = contents
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ShareEditStickerActivity.KEY_SHARE -> {
                if (resultCode == RESULT_OK) activity?.finish()
            }
        }
    }

    companion object {
        fun newInstance() =
            ShareGoalFragment()
    }
}