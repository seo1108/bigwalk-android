package kr.co.bigwalk.app.community

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_INT_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.funding.request.CrewRequestListActivity
import kr.co.bigwalk.app.data.community.GroupMemberResponse
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.databinding.FragmentGroupMemberListBinding
import kr.co.bigwalk.app.util.showToast

class GroupMemberListFragment : Fragment() {
    private lateinit var binding: FragmentGroupMemberListBinding
    private val groupViewItem: GroupMemberListItem by lazy { arguments?.getSerializable(KEY_GROUP_VIEW_ITEM) as GroupMemberListItem }
    private var myInfo: GroupMemberResponse? = null
    private val groupMemberListAdapter = GroupMemberListAdapter {
        if (myInfo?.role == GroupMemberRole.OWNER) {
            showGroupMemberOptionsDialog(it)
        }
        false
    }

    private val groupMemberListViewModel by lazy {
        ViewModelProvider(this, GroupMemberListViewModelFactory(groupViewItem.groupId, groupViewItem.requestJointCount))
            .get(GroupMemberListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_member_list, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_member_view", null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
    }

    override fun onResume() {
        super.onResume()
        groupMemberListViewModel.fetchGroupMemberList()
    }

    private fun setView() {
        with(binding) {
            viewModel = groupMemberListViewModel
            memberListContainer.adapter = groupMemberListAdapter
            myInfoContainer.setOnLongClickListener {
                showMyOptionsDialog()
                false
            }
        }
    }

    private fun bindViewModel() {
        with(groupMemberListViewModel) {
            groupMemberList.observe(viewLifecycleOwner, Observer { list ->
                groupMemberListAdapter.submitList(list)
            })
            myInfoToGroup.observe(viewLifecycleOwner, Observer {
                myInfo = it
                activity?.invalidateOptionsMenu()
            })
            groupInviteDeepLink.observe(viewLifecycleOwner, Observer { deepLink ->
                inviteGroup(deepLink)
            })
            delegationComplete.observe(viewLifecycleOwner, Observer {
                showToast(getString(R.string.success_delegate_auth))
            })
            kickComplete.observe(viewLifecycleOwner, Observer { name ->
                showToast(String.format(getString(R.string.success_kick_member), name))
            })
            leaveComplete.observe(viewLifecycleOwner, Observer {
                showToast(getString(R.string.success_leave_group))
            })
            invalidMember.observe(viewLifecycleOwner, Observer {
                activity?.finish()
            })
            requestCount.observe(viewLifecycleOwner, Observer {
            })
        }
    }

    private fun inviteGroup(deepLink: String) {
        android.app.AlertDialog.Builder(context)
            .setTitle("크루 초대 링크를 복사하였어요")
            .setMessage("크루 초대 링크를 통해 크루에 가입 할 수 있습니다.\n초대하고 싶은 멤버에게 링크를 전달해주세요.")
            .setNegativeButton(R.string.confirm) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("전달하기") { dialog, _ ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, deepLink)
                }
                startActivity(Intent.createChooser(intent, "친구 초대"))
            }
            .create().show()
    }

    private fun showGroupMemberOptionsDialog(member: GroupMemberResponse) {
        val list = resources.getStringArray(R.array.dialog_group_member)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(member.name)
            .setItems(list) { _, which ->
                when (which) {
                    0 -> {
                        showDelegateGroupOwnerDialog(member.userId)
                    }
                    1 -> {
                        showKickGroupMemberDialog(member)
                    }
                }
            }
            .create()

        dialog.show()
    }

    private fun showMyOptionsDialog() {
        val list = resources.getStringArray(R.array.dialog_group_my)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_leave_group_title))
            .setItems(list) { _, which ->
                when (which) {
                    0 -> {
                        showLeaveGroupDialog()
                    }
                }
            }
            .create()

        dialog.show()
    }

    private fun showDelegateGroupOwnerDialog(userId: Long) {
        val dialog = android.app.AlertDialog.Builder(context)
            .setMessage(getString(R.string.dialog_delegate_group_owner_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                groupMemberListViewModel.delegateGroupOwner(userId, GroupMemberRole.OWNER)
                FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_member_button_auth_transfer_click", null)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
            }
            .create()

        dialog.run {
            show()
            getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showKickGroupMemberDialog(member: GroupMemberResponse) {
        val dialog = android.app.AlertDialog.Builder(context)
            .setMessage(getString(R.string.dialog_kick_group_member_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                groupMemberListViewModel.kickGroupMember(member)
                FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_member_button_ban_click", null)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
            }
            .create()

        dialog.run {
            show()
            getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    private fun showLeaveGroupDialog() {
        val dialog = android.app.AlertDialog.Builder(context)
            .setMessage(getString(R.string.dialog_leave_group_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                groupMemberListViewModel.leaveGroup()
                FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_member_button_out_click", null)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
            }
            .create()

        dialog.run {
            show()
            getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    private val ITEM_ID_INVITE = 1
    private val ITEM_ID_REQUEST = 2
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(0, ITEM_ID_INVITE, Menu.FIRST + 1, null).setIcon(R.drawable.aos_icon_group_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if (myInfo?.isOwner() == true) {
            if (groupViewItem.requestJointCount > 0)
                menu.add(0, ITEM_ID_REQUEST, Menu.FIRST, null)
                    .setIcon(R.drawable.aos_icon_group_approve_alert)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            else
                menu.add(0, ITEM_ID_REQUEST, Menu.FIRST, null)
                    .setIcon(R.drawable.aos_icon_group_approve)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ITEM_ID_INVITE -> {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("group_member_button_join_request_click", Bundle())
                groupMemberListViewModel.setGroupDeepLink(groupViewItem.groupId, groupViewItem.groupName, groupViewItem.groupImagePath)
            }
            ITEM_ID_REQUEST -> {
                context?.let {
                    startActivityForResult(CrewRequestListActivity.getIntent(it, groupViewItem.groupId, groupViewItem.requestJointCount), KEY_REQUEST_LIST)
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEY_REQUEST_LIST && resultCode == AppCompatActivity.RESULT_OK) {
            groupViewItem.requestJointCount = data?.getIntExtra(CrewRequestListActivity.KEY_REQUEST_COUNT, DEF_INT_VALUE) ?: DEF_INT_VALUE
            activity?.invalidateOptionsMenu()
        }
    }

    companion object {
        private const val KEY_REQUEST_LIST = 1
        private const val KEY_GROUP_VIEW_ITEM = "GROUP_VIEW_ITEM"

        fun newInstance(item: GroupMemberListItem) =
            GroupMemberListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_GROUP_VIEW_ITEM, item)
                }
            }
    }
}
data class GroupMemberListItem(
    val groupId: Long,
    val groupName: String,
    val groupImagePath: String,
    var requestJointCount: Int
): java.io.Serializable