package kr.co.bigwalk.app.campaign.ranking.report

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.campaign.ranking.RankingPlusViewModel
import kr.co.bigwalk.app.databinding.FrameMyRankingBinding

class MyRankingFragment(private val viewModel: RankingPlusViewModel): Fragment() {
    companion object {
        fun newInstance(viewModel: RankingPlusViewModel) = MyRankingFragment(viewModel)
    }

    private lateinit var onPropertyChangedCallback: Observable.OnPropertyChangedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameMyRankingBinding = DataBindingUtil.inflate(inflater, R.layout.frame_my_ranking, container, false)
        binding.viewModel = viewModel
        onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val data = binding.myRankingNumber.text
                if (data=="-") {
                    binding.myRankingNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                } else {
                    binding.myRankingNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                }
            }
        }
        viewModel.myReport.addOnPropertyChangedCallback(onPropertyChangedCallback)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.myReport.removeOnPropertyChangedCallback(onPropertyChangedCallback)
    }
}