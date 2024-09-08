package kr.co.bigwalk.app.community

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentGroupGoalBinding

class GroupGoalFragment : Fragment() {
    private lateinit var binding: FragmentGroupGoalBinding
    private val communityInfoViewModel: CommunityInfoViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CommunityInfoViewModel::class.java)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_goal, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_goal_step_view", null)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        bindViewModel()
    }
    
    override fun onResume() {
        super.onResume()
        if (!binding.animationView.isAnimating) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(100L)
                binding.animationView.resumeAnimation()
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (binding.animationView.isAnimating)
            binding.animationView.pauseAnimation()
    }
    
    private fun setView() {
        with(binding) {
            viewModel = communityInfoViewModel
            goalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    communityInfoViewModel.setProgressGoalStep(progress)
                    communityInfoViewModel.setGoalSetRotate(progress)
                }
                
                override fun onStartTrackingTouch(p0: SeekBar?) {
                    communityInfoViewModel.setBarTrackingStatus(true)
                }
                
                override fun onStopTrackingTouch(p0: SeekBar?) {
                    communityInfoViewModel.setBarTrackingStatus(false)
                }
                
            })
            animationView.setOnClickListener {
                waveAnimation.run {
                    pauseAnimation()
                    progress = 0f
                    playAnimation()
                }
            }
            goalGuideContainer.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.goal_set_info_url))
                })
            }
        }
    }
    
    private fun bindViewModel() {
        with(communityInfoViewModel) {
            goalAnimationRow.observe(this@GroupGoalFragment, Observer<Int> {
                binding.animationView.setAnimation(it)
                binding.animationView.playAnimation()
            })
        }
    }
    
    companion object {
        fun newInstance() = GroupGoalFragment()
    }
}