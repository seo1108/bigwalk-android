package kr.co.bigwalk.app.campaign.ranking

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.RankerBySeasonAdapter
import kr.co.bigwalk.app.databinding.FragmentRankerBySeasonBinding
import kr.co.bigwalk.app.extension.dialogFragmentResize

class RankerBySeasonDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentRankerBySeasonBinding
    private val rankingBySeasonViewModel by lazy {
        ViewModelProvider(this, RankerBySeasonViewModelFactory(arguments?.getString(KEY_SEASON).orEmpty())).get(RankerBySeasonViewModel::class.java)
    }
    private val rankerBySeasonAdapter by lazy { RankerBySeasonAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ranker_by_season, container, false)
        binding.lifecycleOwner = this
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            rankingRecycler.adapter = rankerBySeasonAdapter
            viewModel = rankingBySeasonViewModel
        }
    }

    private fun bindViewModel() {
        with(rankingBySeasonViewModel) {
            rankerInfo.observe(viewLifecycleOwner, Observer {
                rankerBySeasonAdapter.submitList(it.rankers)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().dialogFragmentResize(this@RankerBySeasonDialogFragment, 0.88f, 0.62f)
    }

    companion object {
        private const val KEY_SEASON = "SEASON"
        fun newInstance(seasonKey: String) =
            RankerBySeasonDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SEASON, seasonKey)
                }
            }
    }
}