package kr.co.bigwalk.app.sign_in.organization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_organization_form.*
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.all.AllActivity
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.organization.Content
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.SubDepartment
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.databinding.ActivityOrganizationFormBinding
import kr.co.bigwalk.app.my_page.ModifyMyPageViewModel
import kr.co.bigwalk.app.my_page.MyPageActivity
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_DEPARTMENT_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_ORGANIZATION_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_SEARCH_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_SUBDEPARTMENT1_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_SUBDEPARTMENT2_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_SUBDEPARTMENT3_REQUEST_CODE
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel.Companion.SELECT_SUBDEPARTMENT4_REQUEST_CODE
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.regex.Pattern

class OrganizationFormActivity: AppCompatActivity(), OrganizationNavigator {

    private lateinit var binding: ActivityOrganizationFormBinding
    private lateinit var viewModel: OrganizationViewModel
    private val modifyMyPageViewModel by viewModels<ModifyMyPageViewModel>()
    private var groupId: Long = -1
    private var campaignId: Long = -1

    companion object{
        var firebaseAnalytics : FirebaseAnalytics? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val request = intent.getSerializableExtra("SignUpUserRequest")
        val profileFile = intent.getSerializableExtra("ProfileFile")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_organization_form)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.logEvent("sign_up_profile_b2b_view", Bundle())

        // 딥링크를 통해 들어온 경우 groupId 획득
        groupId = intent.getLongExtra("deep_link_group_id", -1)
        campaignId = intent.getLongExtra("campaign_id", -1)

        viewModel = OrganizationViewModel()

        viewModel.navigator = this

        if ("ModProfile" == intent.getStringExtra("CameFrom")) {
            RemoteApiManager.getUserApi().getMyProfile()
                .enqueue(object : Callback<BaseResponse<MyProfileResponse>> {
                    override fun onResponse(call: Call<BaseResponse<MyProfileResponse>>, response: Response<BaseResponse<MyProfileResponse>>) {
                        when (response.body()?.result) {
                            Result.SUCCESS -> {
                                response.body()?.data?.let {
                                    viewModel.setUserOrganizationData(it)
                                }
                            }
                            Result.FAIL -> {
                                response.body()?.message?.let {

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse<MyProfileResponse>>, t: Throwable) {

                    }

                })
        } else if ("DeepLink" == intent.getStringExtra("CameFrom")) {
            RemoteApiManager.getUserApi().getMyProfile()
                .enqueue(object : Callback<BaseResponse<MyProfileResponse>> {
                    override fun onResponse(call: Call<BaseResponse<MyProfileResponse>>, response: Response<BaseResponse<MyProfileResponse>>) {
                        when (response.body()?.result) {
                            Result.SUCCESS -> {
                                response.body()?.data?.let {
                                    if (it.organization!!.id == 0L) {
                                        // 기업회원 전환
                                        viewModel.setSpaceGroupOrganization(groupId)
                                        if (campaignId > 0) viewModel.setWhereToGo(campaignId)
                                    } else if (groupId == it.organization!!.id) {
                                        // 이미 기업회원이고 해당 기업에 이미 가입되어 있는 경우
                                        if (campaignId > 0) {
                                            goCampaignDetail()
                                        } else {
                                            showToast("이미 가입한 기업입니다.")
                                            finish()
                                        }
                                     } else {
                                        showDeleteGroupAlertDialog()
                                    }
                                    //viewModel.setUserOrganizationData(it)
                                    /*if (it.organization!!.id == intent.getLongExtra("groupId", -1)) {
                                        viewModel.setUserOrganizationData(it)
                                    } else {
                                        showDeleteGroupAlertDialog()
                                    }*/
                                }
                            }
                            Result.FAIL -> {
                                response.body()?.message?.let {

                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse<MyProfileResponse>>, t: Throwable) {

                    }

                })
        }
        else {

            if (request != null) {
                val signUpUserRequest: SignUpUserRequest = request as SignUpUserRequest
                DebugLog.d(signUpUserRequest.toString())
                viewModel.signUpUserRequest = signUpUserRequest
            }
            if (profileFile != null) {
                DebugLog.d("profileFile is not null")
                viewModel.profileFile = profileFile as File
            }


        }

        binding.viewModel = viewModel

        binding.family.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                //영문 숫자 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐ㄱ-ㅣ가-힣ᆢᆞ\\u318d\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55\\s?!]+$")
                if (!ps.matcher(src).matches()) {
                    return@InputFilter ""
                } else{
                    return@InputFilter null
                }
            }
        )

        binding.name.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                //영문 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-흐ㄱ-ㅣ가-힣ᆢᆞ\\u318d\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55\\s?!]+$")
                if (!ps.matcher(src).matches()) {
                    return@InputFilter ""
                } else{
                    return@InputFilter null
                }
            }
        )

        binding.address.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                //영문 숫자 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐ㄱ-ㅣ가-힣ᆢᆞ\\u318d\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55\\s?!]+$")
                if (!ps.matcher(src).matches()) {
                    return@InputFilter ""
                } else{
                    return@InputFilter null
                }
            }
        )



        binding.studentId.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                //영문 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[0-9@#$%&~/|?+-_=!*()^@><{}\\[\\]ᆢᆞ]*$")
                if (!ps.matcher(src).matches()) {
                    return@InputFilter ""
                } else{
                    return@InputFilter null
                }
            }
        )

        binding.employeeAuthNum.filters = arrayOf(
            InputFilter { src, start, end, dst, dstart, dend ->
                //영문 한글 천지인 middle dot[ᆞ]
                val ps = Pattern.compile("^[a-zA-Z0-9ᆢᆞ\\u318d\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55\\s?!]+$")
                if (!ps.matcher(src).matches()) {
                    return@InputFilter ""
                } else{
                    return@InputFilter null
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.navigator = this
        if ("ModProfile" == intent.getStringExtra("CameFrom")) {
            viewModel.hasOrganization.set(true)
        } else {
            /*if (viewModel.organization.get()?.name.isNullOrEmpty())
                viewModel.hasOrganization.set(false)
            else
                viewModel.hasOrganization.set(true)*/
            viewModel.hasOrganization.set(false)
        }
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_ORGANIZATION_REQUEST_CODE) {
            if (data?.getSerializableExtra("OrganizationItem") != null) {
                viewModel.afterSelectOrganization(data.getSerializableExtra("OrganizationItem") as Organization)
            }
        } else if (requestCode == SELECT_DEPARTMENT_REQUEST_CODE) {
            if (data?.getSerializableExtra("DepartmentItem") != null) {
                viewModel.afterSelectDepartment(data.getSerializableExtra("DepartmentItem") as Department)
            }
        } else if (requestCode == SELECT_SUBDEPARTMENT1_REQUEST_CODE) {
            if (data?.getSerializableExtra("DepartmentItem") != null) {
                viewModel.afterSelectSubDepartment(data.getSerializableExtra("DepartmentItem") as Department, 1)
            }
        } else if (requestCode == SELECT_SUBDEPARTMENT2_REQUEST_CODE) {
            if (data?.getSerializableExtra("DepartmentItem") != null) {
                viewModel.afterSelectSubDepartment(data.getSerializableExtra("DepartmentItem") as Department, 2)
            }
        } else if (requestCode == SELECT_SUBDEPARTMENT3_REQUEST_CODE) {
            if (data?.getSerializableExtra("DepartmentItem") != null) {
                viewModel.afterSelectSubDepartment(data.getSerializableExtra("DepartmentItem") as Department, 3)
            }
        } else if (requestCode == SELECT_SUBDEPARTMENT4_REQUEST_CODE) {
            if (data?.getSerializableExtra("DepartmentItem") != null) {
                viewModel.afterSelectSubDepartment(data.getSerializableExtra("DepartmentItem") as Department, 4)
            }
        } else if (requestCode == SELECT_SEARCH_REQUEST_CODE) {
            if (data?.getSerializableExtra("ContentItem") != null) {
                viewModel.afterSelectSearchKeyword(data.getSerializableExtra("ContentItem") as Content)
            }
        }
    }

    override fun goCampaignDetail() {
        /*startActivity(CrewCampaignDetailActivity.getIntent(this@OrganizationFormActivity, campaignId))
        finish()*/
        val intent = Intent(this@OrganizationFormActivity, CampaignActivity::class.java)
        intent.putExtra("campaign_id", campaignId)
        startActivity(intent)

        finish()
    }


    private fun showDeleteGroupAlertDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.alert_delete_group_sub_title))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                val userInformationIntent = Intent(this@OrganizationFormActivity, MyPageActivity::class.java)
                startActivity(userInformationIntent)

                finish()
            }
            .create()

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /*val intent = Intent(this@OrganizationFormActivity, AllActivity::class.java)
        intent.putExtra("deep_link_group_id", groupId)
        startActivity(intent)*/
    }
}