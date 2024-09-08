package kr.co.bigwalk.app.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_community.*
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupShareResponse.Companion.GROUP_NAME_MAX_LENGTH
import kr.co.bigwalk.app.databinding.ActivityCommunityBinding
import kr.co.bigwalk.app.extension.ellipsizeByMaxLength
import kr.co.bigwalk.app.extension.ifLet

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community)
        binding.lifecycleOwner = this

        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        ifLet (intent.getStringExtra(CommunityInfoActivity.PARM_GROUP_NAME), intent.getStringExtra(CommunityInfoActivity.PARM_GROUP_IMAGE_PATH)) { (groupName, groupImagePath) ->
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    GroupMemberListFragment.newInstance(
                        GroupMemberListItem(
                            groupId = intent.getLongExtra(CommunityInfoActivity.PARM_GROUP_ID, -1L),
                            groupName = groupName,
                            groupImagePath = groupImagePath,
                            requestJointCount = intent.getIntExtra(CommunityInfoActivity.PARM_JOIN_REQUEST_COUNT, 0)
                        )
                    )
                )
                .commit()
        }
        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = ""
            toolbar_title.text = intent.getStringExtra(CommunityInfoActivity.PARM_GROUP_NAME).orEmpty().ellipsizeByMaxLength(GROUP_NAME_MAX_LENGTH)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {
                return false
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, CommunityActivity::class.java)
    }
}