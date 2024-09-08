package kr.co.bigwalk.app.community

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import gun0912.tedimagepicker.util.ToastUtil
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.adapter.CommunityListAdapter
import kr.co.bigwalk.app.community.create.CreateCommunityActivity
import kr.co.bigwalk.app.data.CodeType
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.ResultDataState
import kr.co.bigwalk.app.databinding.ActivityMyCommunityListBinding

class MyCommunityListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyCommunityListBinding
    private val communityListAdapter by lazy {
        CommunityListAdapter {
            startActivity(CommunityInfoActivity.getIntent(this, it.groupId))
        }
    }
    private val myCommunityListViewModel by lazy {
        ViewModelProvider(this, MyCommunityListViewModelFactory()).get(MyCommunityListViewModel::class.java)
    }
    private val groupJoinHandler = GroupJoinHandler()

    private val inviteGroupId: Long by lazy { intent.getLongExtra(CommunityInfoActivity.PARM_INVITED_GROUP_ID, -1L) }
    private val inviteGroupKey: String? by lazy { intent.getStringExtra(CommunityInfoActivity.PARM_INVITED_GROUP_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_community_list)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("community_list_view", null)
        setView()
        bindLiveData()
    }

    private fun setView() {
        with(binding) {
            context = this@MyCommunityListActivity
            viewModel = myCommunityListViewModel
            crewList.adapter = communityListAdapter

            var scrollDy = crewList.paddingTop
            val maxDist = crewList.paddingTop
            crewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    scrollDy -= dy
                    image.alpha = scrollDy.toFloat() / maxDist
                }
            })

            createCrewBtn.setOnClickListener {
                startActivity(CreateCommunityActivity.getIntent(this@MyCommunityListActivity))
            }
        }
    }

    private fun bindLiveData() {
        with(myCommunityListViewModel) {
            myCrewList.observe(this@MyCommunityListActivity, Observer {
                communityListAdapter.submitList(it.data)
            })
            joinResult.observe(this@MyCommunityListActivity, Observer<ResultDataState> {
                if (it.result) it.data?.let { groupId ->
                    if (groupId is Long) {
                        PreferenceManager.saveGroupId(groupId)
                        moveToCommunityInfo(groupId)
                    }
                }
                else it.message?.let { ToastUtil.showToast(it) }
            })
        }
        with(groupJoinHandler) {
            inviteResult.observe(this@MyCommunityListActivity, Observer {
                if (it.result) {
                    showJoinGroupDialogAndJoinGroup(it.message, inviteGroupId, inviteGroupKey)
                } else {
                    showAlertDialog(it.code, it.message)
                }
            })
            checkPossibleJoinGroup(inviteGroupId, inviteGroupKey)
        }
    }

    override fun onStart() {
        super.onStart()
        myCommunityListViewModel.getCrewList()
    }

    private fun moveToCommunityInfo(groupId: Long) {
        startActivity(CommunityInfoActivity.getIntent(this, groupId))
    }

    private fun showJoinGroupDialogAndJoinGroup(message: String?, groupId: Long, key: String?) {
        FirebaseAnalytics.getInstance(this).logEvent("pop_up_group_invite_view", null)
        message?.let { message ->
            val dialog = AlertDialog.Builder(this@MyCommunityListActivity)
                .setMessage(message)
                .setPositiveButton(R.string.join) { dialog, _ ->
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, groupId.toString())
                    myCommunityListViewModel.joinGroup(groupId, key)
                    FirebaseAnalytics.getInstance(this).logEvent("pop_up_invite_button_enter_click", bundle)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.run {
                show()
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            }
        }
    }

    private fun showAlertDialog(code: Int, message: String?) {
        FirebaseAnalytics.getInstance(this).logEvent("pop_up_group_sign_up_fail_view", null)
        when (CodeType.fromInt(code)) {
            CodeType.INVALID_AUTHORITY_GROUP_IS_FULL -> FirebaseAnalytics.getInstance(this).logEvent("pop_up_group_sign_up_fail_view", null)
            CodeType.INVALID_AUTHORITY_ALREADY_JOINED_GROUP -> {
                startActivity(CommunityInfoActivity.getIntent(this, inviteGroupId))
                FirebaseAnalytics.getInstance(this).logEvent("pop_up_group_sign_up_fail_view", null)
                return
            }
            else -> FirebaseAnalytics.getInstance(this).logEvent("pop_up_group_sign_up_unknown_view", null)
        }
        message?.let { message ->
            AlertDialog.Builder(this@MyCommunityListActivity)
                .setMessage(message)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, MyCommunityListActivity::class.java)
    }
}