package kr.co.bigwalk.app.campaign.ranking.info

import android.app.Activity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityRankingInformationBinding
import kr.co.bigwalk.app.util.TopAlignSuperscriptSpan

class RankingInformationActivity: AppCompatActivity(), BaseNavigator {
    private lateinit var viewModel: RankingInformationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRankingInformationBinding = DataBindingUtil.setContentView(this, R.layout.activity_ranking_information)
        viewModel = RankingInformationViewModel(this)
        binding.viewModel = viewModel

        val titleText = resources.getString(R.string.ranking_plus)
        val titleSpannable = SpannableString(titleText)
        titleSpannable.setSpan(TopAlignSuperscriptSpan(), titleText.length-1, titleText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.infoTitle.text = titleSpannable

        val btnText = resources.getString(R.string.grade_info_ranking_plus_enjoying)
        val btnSpannable = SpannableString(btnText)
        btnSpannable.setSpan(TopAlignSuperscriptSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.infoMoveBtn.text = btnSpannable

        Glide.with(this)
            .asBitmap()
            .load(R.drawable.ranking_information_image)
            .placeholder(R.drawable.ranking_information_image_low)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(binding.infoImg)
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onBackPressed() {
        viewModel.finish()
    }
}