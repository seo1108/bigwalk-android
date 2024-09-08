package kr.co.bigwalk.app.community.create

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.adapter.SelectInterestCategoryAdapter
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.databinding.FragmentDialogSelectInterestBinding
import kr.co.bigwalk.app.util.EventObserver

class SelectInterestDialogFragment(private val selectTagList: List<CrewConcernTagResponse>) : DialogFragment() {
    private lateinit var binding: FragmentDialogSelectInterestBinding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateCommunityViewModel::class.java)
    }
    private val selectInterestViewModel by lazy {
        ViewModelProvider(this).get(SelectInterestViewModel::class.java)
    }
    private val selectInterestCategoryAdapter by lazy { SelectInterestCategoryAdapter(selectInterestViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_select_interest, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            exitButton.setOnClickListener {
                dismiss()
            }
            saveButton.setOnClickListener {
                selectInterestViewModel.validationCheck()
            }
        }
    }

    private fun setView() {
        with(binding) {
            categoryContainer.adapter = selectInterestCategoryAdapter
        }
    }

    private fun bindViewModel() {
        with(selectInterestViewModel) {
            fetchCrewConcern(selectTagList)
            crewConcernList.observe(viewLifecycleOwner, Observer {
                selectInterestCategoryAdapter.submitList(it)
            })
            saveSuccess.observe(viewLifecycleOwner, EventObserver {
                createCommunityViewModel.setCrewConcern(it)
                dismiss()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }
}