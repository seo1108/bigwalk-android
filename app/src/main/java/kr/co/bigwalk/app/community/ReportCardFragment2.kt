package kr.co.bigwalk.app.community

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentReport2Binding

class ReportCardFragment2 : Fragment() {
    private lateinit var binding: FragmentReport2Binding
    private val communityInfoViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CommunityInfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_2, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setView()
    }
    
    private fun setView() {
        with(binding) {
            viewModel = communityInfoViewModel
        }
    }
    
    companion object {
        fun newInstance() = ReportCardFragment2()
    }
}