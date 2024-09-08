package kr.co.bigwalk.app.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.BottomSheetFeedOptionBinding

class FeedOptionDialogFragment(private val mine: Boolean, private val callback: OptionClickListener) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFeedOptionBinding

    interface OptionClickListener {
        fun onBlameClick()
        fun onModifyClick()
        fun onDeleteClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_feed_option, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            mine = this@FeedOptionDialogFragment.mine
            blameButton.setOnClickListener {
                callback.onBlameClick()
                dismiss()
                FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_btn_Declaration_click", Bundle())
            }
            editButton.setOnClickListener {
                callback.onModifyClick()
                dismiss()
                FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_btn_contents_edit_click", Bundle())
            }
            deleteButton.setOnClickListener {
                callback.onDeleteClick()
                dismiss()

            }
        }
    }


    companion object {
        fun newInstance(mine: Boolean, callback: OptionClickListener) =
            FeedOptionDialogFragment(mine, callback)
    }
}