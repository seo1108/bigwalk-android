package kr.co.bigwalk.app.lock_screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.linecorp.apng.ApngDrawable
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FrameLockScreenRankBinding
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.MainThreadExecutor
import kr.co.bigwalk.app.util.SingleThreadExecutor

class LockScreenRankFragment(
    private val lockScreenViewModel: LockScreenViewModel
) : Fragment() {

    private var characterDrawable: ApngDrawable? = null
    private var mainButtonSelectAnimation: ApngDrawable? = null
    private lateinit var binding : FrameLockScreenRankBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate (inflater, R.layout.frame_lock_screen_rank, container, false)
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }

        playAnimation()
        binding.viewModel = lockScreenViewModel
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

    override fun onResume() {
        super.onResume()
        DebugLog.d("왜안되냐")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainButtonSelectAnimation?.recycle()
        characterDrawable?.recycle()
    }
}