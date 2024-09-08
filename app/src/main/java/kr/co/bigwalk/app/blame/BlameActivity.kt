package kr.co.bigwalk.app.blame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.databinding.ActivityBlameBinding
import kr.co.bigwalk.app.feed.BlameViewModel

class BlameActivity : BaseActivity<ActivityBlameBinding>(R.layout.activity_blame) {

    private val blameViewModel by viewModels<BlameViewModel>()
    private val contentId: Long by lazy { intent.getLongExtra(KEY_CONTENT_ID, DEF_LONG_VALUE) }
    private val userId: Long by lazy { intent.getLongExtra(KEY_USER_ID, DEF_LONG_VALUE) }
    private val blameType: BlameType by lazy { intent.getSerializableExtra(KEY_BLAME_TYPE) as BlameType }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(R.id.blame_container, FeedBlameFragment.newInstance(blameType.title))
            .commit()

        bindViewModel()
    }

    private fun bindViewModel() {
        with(blameViewModel) {
            initId(contentId, userId, this@BlameActivity.blameType)
        }
    }

    companion object {
        private const val KEY_CONTENT_ID = "CONTENT_ID"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_BLAME_TYPE = "BLAME_TYPE"
        fun getIntent(context: Context, contentId: Long, userId: Long, blameType: BlameType) =
            Intent(context, BlameActivity::class.java).apply {
                putExtra(KEY_CONTENT_ID, contentId)
                putExtra(KEY_USER_ID, userId)
                putExtra(KEY_BLAME_TYPE, blameType)
            }
    }
}

enum class BlameType(val title: String) {
    FEED("게시글 신고"),
    FEED_COMMENT("댓글 신고"),
    FUNDING_COMMENT("댓글 신고"),
    USER("유저 신고"),
    NONE("신고")
}