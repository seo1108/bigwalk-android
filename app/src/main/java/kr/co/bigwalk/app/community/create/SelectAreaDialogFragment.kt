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
import kr.co.bigwalk.app.community.adapter.SelectAddress1DepthAdapter
import kr.co.bigwalk.app.community.adapter.SelectAddress2DepthAdapter
import kr.co.bigwalk.app.databinding.FragmentDialogSelectAreaBinding
import kr.co.bigwalk.app.util.EventObserver

class SelectAreaDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogSelectAreaBinding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateCommunityViewModel::class.java)
    }
    private val selectAreaViewModel by lazy {
        ViewModelProvider(this).get(SelectAreaViewModel::class.java)
    }

    private val selectAddress1DepthAdapter by lazy {
        SelectAddress1DepthAdapter {
            selectAreaViewModel.filterAddress(it)
        }
    }
    private val selectAddress2DepthAdapter by lazy {
        SelectAddress2DepthAdapter {
            selectAreaViewModel.address2Depth = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_select_area, container, false)
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
                selectAreaViewModel.validationCheck()
            }
        }
    }

    private fun setView() {
        with(binding) {
            address1depth.adapter = selectAddress1DepthAdapter
            address2depth.adapter = selectAddress2DepthAdapter
            address1depth.itemAnimator = null
            address2depth.itemAnimator = null
        }
    }


    private fun bindViewModel() {
        var address1Depth = ""
        var address2Depth = ""
        with(createCommunityViewModel) {
            secondCrewAddress1.observe(viewLifecycleOwner, Observer {
                address1Depth = it.first
                address2Depth = it.second
            })
        }
        with(selectAreaViewModel) {
            crewFirstAddressResponse.observe(viewLifecycleOwner, Observer {
                selectAddress1DepthAdapter.submitList(it)
                selectAddress1DepthAdapter.setItemClick(address1Depth)
            })
            crewSecondAddressResponse.observe(viewLifecycleOwner, Observer {
                selectAddress2DepthAdapter.run {
                    reselectPosition()
                    submitList(it)
                    selectAddress2DepthAdapter.setItemClick(address2Depth)
                }
            })
            saveSuccess.observe(viewLifecycleOwner, EventObserver {
                createCommunityViewModel.setCrewAddress(it.first, it.second)
                dismiss()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    }

    companion object {
        fun newInstance() =
            SelectAreaDialogFragment()
    }
}