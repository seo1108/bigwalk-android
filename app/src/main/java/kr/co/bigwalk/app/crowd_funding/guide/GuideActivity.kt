package kr.co.bigwalk.app.crowd_funding.guide

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide)
        binding.lifecycleOwner = this

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setView()
    }

    private fun setView() {
        with(binding) {
            toolbarTitle.text = intent.getStringExtra(KEY_TITLE).orEmpty()
            exitButton.setOnClickListener {
                finish()
            }
            Glide.with(this@GuideActivity)
                .asBitmap()
                .load(intent.getStringExtra(KEY_DETAIL_IMAGE_PATH))
                .placeholder(R.drawable.ranking_information_image_low)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(guideImage)
        }
    }

    companion object {
        private const val KEY_DETAIL_IMAGE_PATH = "DETAIL_IMAGE_PATH"
        private const val KEY_TITLE = "TITLE"
        fun getIntent(context: Context, detailImagePath: String, title: String) =
            Intent(context, GuideActivity::class.java).apply {
                putExtra(KEY_DETAIL_IMAGE_PATH, detailImagePath)
                putExtra(KEY_TITLE, title)
            }
    }
}