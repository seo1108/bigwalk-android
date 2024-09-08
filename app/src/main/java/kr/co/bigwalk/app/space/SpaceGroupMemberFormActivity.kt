package kr.co.bigwalk.app.space

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.all.AllActivity
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.databinding.ActivitySpaceGroupMemberFormBinding
import kr.co.bigwalk.app.sign_in.agreement.AgreementWithEnterprisePrivacyActivity
import kr.co.bigwalk.app.util.showToast

class SpaceGroupMemberFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpaceGroupMemberFormBinding
    private lateinit var spaceGroupMemberFormViewModel: SpaceGroupMemberFormViewModel
    private var groupId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_space_group_member_form)
        binding.lifecycleOwner = this

        spaceGroupMemberFormViewModel = ViewModelProvider(this).get(SpaceGroupMemberFormViewModel::class.java)
        binding.viewModel = spaceGroupMemberFormViewModel

        if (PreferenceManager.getOrganizationName().isNotEmpty()) {
            showDeleteGroupAlertDialog()
        }
        groupId = intent.getLongExtra("deep_link_group_id", -1)
        spaceGroupMemberFormViewModel.fetchSpaceGroup(groupId)

        setView()
        setViewModel()
    }

    private fun setView() {
        with(binding) {
            agreementServiceDocs.setOnClickListener {
                val intent = Intent(this@SpaceGroupMemberFormActivity, AgreementWithEnterprisePrivacyActivity::class.java)
                intent.putExtra("organization", Organization(id = null, name = organization.text.toString()))
                startActivity(intent)
            }
            corporateFormBackImage.setOnClickListener {
                onBackPressed()
            }
            organizationFormConfirmButton.setOnClickListener {
                spaceGroupMemberFormViewModel.changeOrganizationAccount(
                    binding.content1.text.toString(),
                    binding.content2.text.toString(),
                    binding.content3.text.toString(),
                    binding.content4.text.toString(),
                    binding.content5.text.toString()
                )
            }
        }
    }

    private fun setViewModel() {
        with(spaceGroupMemberFormViewModel) {
            successEvent.observe(this@SpaceGroupMemberFormActivity, Observer {
                showToast(getString(R.string.success_change_group))
                finish()
                val campaignId = intent.getLongExtra("campaign_id", -1L)
                if (campaignId >= 0) {
                    val intent = Intent(this@SpaceGroupMemberFormActivity, CampaignActivity::class.java)
                    intent.putExtra("campaign_id", campaignId)
                    startActivity(intent)
                }
            })
            expiredGroup.observe(this@SpaceGroupMemberFormActivity, Observer { groupName ->
                if (PreferenceManager.getOrganizationName().isEmpty()) {
                    showExpiredDateAlertDialog(groupName)
                }
            })
        }
    }

    private fun showExpiredDateAlertDialog(groupName: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(String.format(getString(R.string.alert_expire_group_sub_title), groupName))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                finish()
            }
            .create()

        dialog.show()
    }

    private fun showDeleteGroupAlertDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.alert_delete_group_sub_title))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                onBackPressed()
            }
            .create()

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@SpaceGroupMemberFormActivity, AllActivity::class.java)
        intent.putExtra("deep_link_group_id", groupId)
        startActivity(intent)
    }
}