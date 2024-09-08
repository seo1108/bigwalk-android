package kr.co.bigwalk.app.walk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.linecorp.apng.ApngDrawable
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FrameWalk2Binding
import kr.co.bigwalk.app.util.MainRandomViewMaker
import kr.co.bigwalk.app.util.MainThreadExecutor
import kr.co.bigwalk.app.util.SingleThreadExecutor


class WalkFragment2 : Fragment() {

    private var characterDrawable: ApngDrawable? = null
    private var mainButtonSelectAnimation: ApngDrawable? = null
    private lateinit var binding: FrameWalk2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.frame_walk_2,
            container,
            false
        )
        val walkActivity = activity as WalkActivity
        binding.viewModel = walkActivity.viewModel
        binding.walkRandomText.text = MainRandomViewMaker.getRandomComment()
        playAnimation()
        WalkActivity.firebaseAnalytics?.logEvent("ranking_view", Bundle())
        return binding.root
    }

    private fun playAnimation() {
        SingleThreadExecutor().execute {
            if(characterDrawable == null || characterDrawable?.isRecycled == true) characterDrawable = ApngDrawable.decode(activity!!.assets, "apng_b.png")
            MainThreadExecutor().execute {
                characterDrawable?.clearAnimationCallbacks()
                binding.walkMovingCharacter.setImageDrawable(null)
                characterDrawable?.loopCount = ApngDrawable.LOOP_FOREVER
                characterDrawable?.setTargetDensity(resources.displayMetrics)
                binding.walkMovingCharacter.setImageDrawable(characterDrawable)
                characterDrawable?.start()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainButtonSelectAnimation?.recycle()
        characterDrawable?.recycle()
    }
}