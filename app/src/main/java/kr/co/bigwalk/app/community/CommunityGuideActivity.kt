package kr.co.bigwalk.app.community

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityCommunityGuideBinding
import kr.co.bigwalk.app.walk.WalkActivity

class CommunityGuideActivity : AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCommunityGuideBinding = DataBindingUtil.setContentView(this, R.layout.activity_community_guide)

        binding.infoBack.setOnClickListener { finish() }
        binding.infoMoveBtn.setOnClickListener {
            finish()
            moveToGroup(
                intent.getLongExtra(CommunityInfoActivity.PARM_INVITED_GROUP_ID, -1L),
                intent.getStringExtra(CommunityInfoActivity.PARM_INVITED_GROUP_KEY)
            )
        }

        Glide.with(this)
            .asBitmap()
            .load(R.drawable.community_guide)
            .placeholder(R.drawable.ranking_information_image_low)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(binding.infoImg)

        WalkActivity.firebaseAnalytics?.logEvent("community_notice_view", Bundle())
    }

    override fun getContext(): Activity {
        return this
    }

    private fun moveToGroup(invitedGroupId: Long? = null, key: String? = null) {
        val intent = MyCommunityListActivity.getIntent(this)
        invitedGroupId?.let { intent.putExtra(CommunityInfoActivity.PARM_INVITED_GROUP_ID, it) }
        key?.let { intent.putExtra(CommunityInfoActivity.PARM_INVITED_GROUP_KEY, it) }
        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, CommunityGuideActivity::class.java)
    }
}