package kr.co.bigwalk.app.sign_up

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.databinding.FragmentSelectDetailInfoBinding
import kr.co.bigwalk.app.profile.detail.SelectAreaActivity
import kr.co.bigwalk.app.profile.detail.SelectInterestActivity

class SelectDetailInfoFragment : BaseFragment<FragmentSelectDetailInfoBinding>(R.layout.fragment_select_detail_info) {

    private val signUpViewModel by activityViewModels<SignUpViewModel>()

    private val selectDetailInfoViewModel by lazy {
        ViewModelProvider(this).get(SelectDetailInfoViewModel::class.java)
    }
    private var selectArea = Pair<String, String>("","")
    private var selectTagList: List<CrewConcernTagResponse>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("sign_up_detail_info_view", Bundle())
        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = selectDetailInfoViewModel

            formArea.setOnClickListener {
                showSelectAreaDialog()
            }
            formInterest.setOnClickListener {
                showSelectInterestDialog(selectTagList)
            }
        }
    }

    private fun bindViewModel() {
        with(selectDetailInfoViewModel) {
            selectConcernList.observe(viewLifecycleOwner, Observer { list ->
                selectTagList = list
                binding.interestList.removeAllViews()
                list.forEach { tagList ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.view_crew_create_tag, binding.interestList, false)

                    val tagView: TextView = view.findViewById(R.id.tag)
                    tagView.text = tagList.name


                    binding.interestList.addView(view)
                }
            })
        }
        with(signUpViewModel) {
            area.observe(viewLifecycleOwner, Observer {
                selectArea = it
                selectDetailInfoViewModel.setArea(it)
            })
            selectConcernList.observe(viewLifecycleOwner, Observer { list ->
                selectDetailInfoViewModel.setConcern(list)
            })
        }
    }

    private fun showSelectAreaDialog() {
        startActivityForResult(SelectAreaActivity.getIntent(requireContext(), selectArea.first, selectArea.second), REQ_CODE_AREA)
    }

    private fun showSelectInterestDialog(selectTagList: List<CrewConcernTagResponse>?) {
        startActivityForResult(SelectInterestActivity.getIntent(requireContext(), selectTagList.orEmpty()), REQ_CODE_INTEREST)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("sign_up_detail_info_interest_view", Bundle())
    }

    override fun onResume() {
        super.onResume()
        signUpViewModel.setFunctionAndViewForScreen(2)
        (activity as SignUpActivity).supportActionBar?.title = getString(R.string.select_details)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_AREA -> {
                if (resultCode == Activity.RESULT_OK) {
                    val area = data?.getSerializableExtra(SelectAreaActivity.KEY_SELECT_AREA) as Pair<String, String>
                    signUpViewModel.area.value = area
                }
            }
            REQ_CODE_INTEREST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = data?.getSerializableExtra(SelectInterestActivity.KEY_SELECT_TAG_LIST) as List<CrewConcernTagResponse>?
                    signUpViewModel.selectConcernList.value = list
                }
            }
        }
    }

    companion object {
        private const val REQ_CODE_AREA = 100
        private const val REQ_CODE_INTEREST = 101
        fun newInstance() =
            SelectDetailInfoFragment()
    }
}