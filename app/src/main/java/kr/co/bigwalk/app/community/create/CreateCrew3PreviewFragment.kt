package kr.co.bigwalk.app.community.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentCreateCrew3PreviewBinding

class CreateCrew3PreviewFragment : Fragment() {
    private lateinit var binding: FragmentCreateCrew3PreviewBinding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateCommunityViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_crew_3_preview, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = createCommunityViewModel
        }
    }


    private fun bindViewModel() {
        with(createCommunityViewModel) {
            secondCrewConcernList.observe(viewLifecycleOwner, Observer { list ->
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
    }

    override fun onResume() {
        super.onResume()
        createCommunityViewModel.setFunctionAndViewForScreen(2)
    }


    companion object {
        fun newInstance() = CreateCrew3PreviewFragment()
    }
}