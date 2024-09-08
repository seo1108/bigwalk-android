package kr.co.bigwalk.app.sign_up.agreement

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.DialogAgreementBinding

class AgreementDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAgreementBinding
    private val agreementDialogViewModel by viewModels<AgreementDialogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_agreement, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            vm = agreementDialogViewModel
            agreementAll.setOnClickListener {
                checkService.isChecked = agreementAll.isChecked
                checkPrivacy.isChecked = agreementAll.isChecked
                checkAge.isChecked = agreementAll.isChecked
                checkMarketing.isChecked = agreementAll.isChecked
                agreementDialogViewModel.agreeWithAllTerms(agreementAll.isChecked)
            }
            checkService.setOnCheckedChangeListener { _, isChecked ->
                agreementDialogViewModel.agreeWithService(isChecked)
            }
            checkPrivacy.setOnCheckedChangeListener { _, isChecked ->
                agreementDialogViewModel.agreeWithPrivacy(isChecked)
            }
            checkAge.setOnCheckedChangeListener { _, isChecked ->
                agreementDialogViewModel.agreeWithOver14(isChecked)
            }
            checkMarketing.setOnCheckedChangeListener { _, isChecked ->
                agreementDialogViewModel.agreeWithMarketing(isChecked)
            }
            btnService.setOnClickListener { showTermsUrl(this@AgreementDialogFragment.getString(R.string.terms_of_service_url)) }
            btnPrivacy.setOnClickListener { showTermsUrl(this@AgreementDialogFragment.getString(R.string.terms_of_privacy_url)) }
            btnMarketing.setOnClickListener { showTermsUrl(this@AgreementDialogFragment.getString(R.string.terms_of_marketing_url)) }
            btnAgreement.setOnClickListener {
                setFragmentResult(SUBMIT, Bundle())
                dismiss()
            }
            changeTheColorOfSpecificString(binding.checkService)
            changeTheColorOfSpecificString(binding.checkPrivacy)
            changeTheColorOfSpecificString(binding.checkAge)
        }
    }

    private fun changeTheColorOfSpecificString(view: TextView) {
        val text = view.text.toString()
        val span = SpannableString(text)
        span.setSpan(ForegroundColorSpan(Color.parseColor("#f15060")), text.length - 4, text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        view.text = span
    }

    private fun showTermsUrl(url: String) {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    companion object {
        const val SUBMIT = "submit"
        fun newInstance() =
            AgreementDialogFragment()
    }
}