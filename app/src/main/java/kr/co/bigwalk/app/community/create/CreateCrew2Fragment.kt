package kr.co.bigwalk.app.community.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.databinding.FragmentCreateCrew2Binding

class CreateCrew2Fragment : Fragment() {
    private lateinit var binding: FragmentCreateCrew2Binding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateCommunityViewModel::class.java)
    }
    private var selectTagList: List<CrewConcernTagResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_crew_2, container, false)
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
            crewCategorySet.setOnCheckedChangeListener { group, checkedId ->
                familyBtn.text =
                    if (checkedId == R.id.family_btn) SelectorCrewCategoryText.FAMILY.selectedText else SelectorCrewCategoryText.FAMILY.unselectedText
                friendBtn.text =
                    if (checkedId == R.id.friend_btn) SelectorCrewCategoryText.FRIEND.selectedText else SelectorCrewCategoryText.FRIEND.unselectedText
                partnerBtn.text =
                    if (checkedId == R.id.partner_btn) SelectorCrewCategoryText.PARTNER.selectedText else SelectorCrewCategoryText.PARTNER.unselectedText
                clubBtn.text =
                    if (checkedId == R.id.club_btn) SelectorCrewCategoryText.CLUB.selectedText else SelectorCrewCategoryText.CLUB.unselectedText
                coupleBtn.text =
                    if (checkedId == R.id.couple_btn) SelectorCrewCategoryText.COUPLE.selectedText else SelectorCrewCategoryText.COUPLE.unselectedText
                val checkedView = binding.root.findViewById<RadioButton>(checkedId)
                createCommunityViewModel.setJoinUs(checkedView.tag.toString())
            }
            formArea.setOnClickListener {
                showSelectAreaDialog()
            }
            formInterest.setOnClickListener {
                showSelectInterestDialog(selectTagList)
            }
        }
    }

    private fun bindViewModel() {
        with(createCommunityViewModel) {
            secondCrewConcernList.observe(viewLifecycleOwner, Observer { list ->
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
            secondCrewJoinUs.observe(viewLifecycleOwner, Observer {
                val selectView = binding.root.findViewWithTag<RadioButton>(it)
                selectView.isChecked = true
            })
        }
    }

    private fun showSelectAreaDialog() {
        val dialogFragment = SelectAreaDialogFragment.newInstance()
        dialogFragment.show(childFragmentManager, dialogFragment.tag)
    }

    private fun showSelectInterestDialog(selectTagList: List<CrewConcernTagResponse>?) {
        val dialogFragment = SelectInterestDialogFragment(selectTagList.orEmpty())
        dialogFragment.show(childFragmentManager, dialogFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        createCommunityViewModel.setFunctionAndViewForScreen(1)
    }

    enum class SelectorCrewCategoryText(val unselectedText: String, val selectedText: String) {
        FAMILY("물보다 진한 피", "가족"),
        FRIEND("가까워도 보증은 어려워", "친구"),
        PARTNER("회식을 한다면 점심에!", "동료"),
        CLUB("정보 공유, 친목 도모", "동호회"),
        COUPLE("남의 깻잎은 떼주면 안돼", "연인")
    }

    companion object {
        fun newInstance() = CreateCrew2Fragment()
    }
}