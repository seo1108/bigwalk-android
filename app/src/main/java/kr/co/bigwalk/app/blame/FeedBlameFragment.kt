package kr.co.bigwalk.app.blame

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.databinding.FragmentFeedBlameBinding
import kr.co.bigwalk.app.feed.BlameViewModel
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.KeyboardVisibilityUtils

class FeedBlameFragment : BaseFragment<FragmentFeedBlameBinding>(R.layout.fragment_feed_blame) {

    private val blameViewModel by activityViewModels<BlameViewModel>()
    private val toolbarTitle : String by lazy { arguments?.getString(KEY_TOOLBAR_TITLE).orEmpty() }
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_declaration_view", Bundle())
        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = blameViewModel
            exitButton.setOnClickListener {
                activity?.finish()
            }
            toolbarTitle.text = this@FeedBlameFragment.toolbarTitle
            blameCategoryContainer.setOnCheckedChangeListener { group, checkedId ->
//                disableView.isGone = checkedId == R.id.blame_etc
                if (checkedId == R.id.blame_etc) {
                    blameViewModel.setBlameMsg(true, contentView.text.toString())
                    return@setOnCheckedChangeListener
                }
                val checkedView = binding.root.findViewById<RadioButton>(checkedId)
                blameViewModel.setBlameMsg(false, checkedView.text.toString())
                contentView.clearFocus()
                focusOutofKeyboard()
            }
            contentView.addTextChangedListener {
                blameViewModel.setBlameMsg(true, contentView.text.toString())
                blameEtc.isChecked = true
                textLength.text = "${it?.length}/100자"
            }
            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onShowKeyboard = { keyboardHeight ->
                    binding.scrollView.run {
                        smoothScrollTo(scrollX, binding.textLength.bottom-keyboardHeight)
                    }
                })
            btnBlame.setOnClickListener {
                blameViewModel.requestDeclare()
                FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_declaration_btn_declaration_click", Bundle())
            }
        }
    }

    private fun focusOutofKeyboard() {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.contentView.windowToken, 0)
    }

    private fun bindViewModel() {
        with(blameViewModel) {
            successEvent.observe(viewLifecycleOwner, EventObserver {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.blame_container, UserBlockingFragment.newInstance())
                    .commit()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    companion object {
        private const val KEY_TOOLBAR_TITLE = "TOOLBAR_TITLE"
        fun newInstance(title: String) =
            FeedBlameFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TOOLBAR_TITLE, title)
                }
            }
    }
}