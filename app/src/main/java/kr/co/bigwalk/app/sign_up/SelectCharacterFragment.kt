package kr.co.bigwalk.app.sign_up

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.databinding.FragmentSelectCharacterBinding
import kr.co.bigwalk.app.sign_in.character.SelectCharacterActivity

class SelectCharacterFragment : BaseFragment<FragmentSelectCharacterBinding>(R.layout.fragment_select_character) {

    private val signUpViewModel by activityViewModels<SignUpViewModel>()
    private var selectedCharacter: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("sign_up_character_view", Bundle())
        setView()
    }

    private fun setView() {
        with(binding) {
            selectCharacterViewpager.adapter = SelectCharacterActivity.ViewPagerAdapter()
            selectCharacterViewpager.offscreenPageLimit = 3
            val pageMargin = 12f
            val pageOffset = 12f
            val metrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
            var mOffset = 0F
            selectCharacterViewpager.setPageTransformer { page, position ->
                if (metrics.widthPixels <= 720 && metrics.heightPixels <= 1199) mOffset = position * -(2F * pageOffset + pageMargin)
                else if ((metrics.widthPixels > 720 || metrics.widthPixels <= 1080) && (metrics.heightPixels > 1199 || metrics.heightPixels <= 2064))
                    mOffset = 0F
                else mOffset = position * -(pageOffset + pageMargin)
                when {
                    position <= 1 -> {
                        page.translationX = mOffset
                    }
                }
            }

            selectCharacterViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectedCharacter = position + 1
//                    PreferenceManager.saveCharacter("${position + 1}")
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        signUpViewModel.setFunctionAndViewForScreen(0)
        (activity as SignUpActivity).supportActionBar?.title = getString(R.string.character_selection)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            signUpViewModel.setCharacter("$selectedCharacter", ProfileType.CHARACTER)
            PreferenceManager.saveCharacter("$selectedCharacter")
        }
    }

    companion object {
        fun newInstance() =
            SelectCharacterFragment()
    }
}