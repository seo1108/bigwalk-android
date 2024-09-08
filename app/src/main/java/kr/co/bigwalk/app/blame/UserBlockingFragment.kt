package kr.co.bigwalk.app.blame

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.databinding.FragmentUserBlockingBinding
import kr.co.bigwalk.app.feed.BlameViewModel
import kr.co.bigwalk.app.util.EventObserver

class UserBlockingFragment : BaseFragment<FragmentUserBlockingBinding>(R.layout.fragment_user_blocking) {
    private val blameViewModel by activityViewModels<BlameViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_user_block_view", Bundle())
        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            exitButton.setOnClickListener {
                activity?.finish()
            }
            userBlockingContainer.setOnCheckedChangeListener { group, checkedId ->
                blameViewModel.setCheckedBlocking(checkedId == R.id.btn_user_blocking)
            }
            btnBlame.setOnClickListener {
                blameViewModel.requestUserBlocking()
                FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_user_block_btn_block_click", Bundle())
            }
        }
    }

    private fun bindViewModel() {
        with(blameViewModel) {
            successEvent.observe(viewLifecycleOwner, EventObserver {
                activity?.finish()
            })
        }
    }

    companion object {
        fun newInstance() =
            UserBlockingFragment()
    }
}