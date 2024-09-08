package kr.co.bigwalk.app.event.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.databinding.MyMissionFragmentBinding
import kr.co.bigwalk.app.event.EventActivity
import kr.co.bigwalk.app.event.EventViewModel

class MyMissionFragment : Fragment() {
    lateinit var navigator: BaseNavigator
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: MyMissionFragmentBinding
    private lateinit var viewModel: EventViewModel

    companion object {
        fun newInstance(navigator: BaseNavigator, firebaseAnalytics: FirebaseAnalytics): MyMissionFragment {
            val fragment = MyMissionFragment()
            fragment.navigator = navigator
            fragment.firebaseAnalytics = firebaseAnalytics
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyMissionFragmentBinding.inflate(inflater, container, false)
        viewModel = (activity as EventActivity).viewModel
        binding.viewModel = viewModel

        handleEvent()

        return binding.root
    }

    private fun handleEvent() {
        viewModel.isMission1Completed.observe(this, androidx.lifecycle.Observer<Boolean> {
            if (it == true) {
                binding.mission1.isSelected = it
                binding.mission1.translationZ = 0.1f
            }
        })
        viewModel.isMission2Completed.observe(this, androidx.lifecycle.Observer<Boolean> {
            if (it == true) {
                binding.mission2.isSelected = it
                binding.mission2.translationZ = 0.1f
            }
        })
        viewModel.isMissionCompleted.observe(this, androidx.lifecycle.Observer<String> {
            when (it) {
                WelcomeMissionStatus.ONGOING.type -> {
                    binding.eventComplete.isEnabled = false
                    binding.eventComplete.text = resources.getString(R.string.my_mission_complete)
                    binding.eventCompleteDescription.visibility = View.GONE
                }
                WelcomeMissionStatus.COMPLETED.type -> {
                    binding.eventComplete.isEnabled = true
                    binding.eventComplete.text = resources.getString(R.string.my_mission_complete)
                    binding.eventCompleteDescription.visibility = View.VISIBLE
                }
                WelcomeMissionStatus.NONE.type -> {
                    binding.eventComplete.isEnabled = false
                    binding.myMissionDescription.visibility = View.GONE
                    binding.mission1Goal.text = "달성"
                    binding.mission2Goal.text = "달성"
                    binding.myMissionExpiredDate.visibility = View.GONE
                    binding.eventComplete.text = resources.getString(R.string.my_mission_completed)
                    binding.eventCompleteDescription.visibility = View.GONE
                }
            }
        })
    }
}