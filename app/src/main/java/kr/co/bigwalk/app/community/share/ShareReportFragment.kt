package kr.co.bigwalk.app.community.share

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentShareReportBinding
import kr.co.bigwalk.app.util.takeScreenShot
import java.io.ByteArrayOutputStream

class ShareReportFragment : Fragment() {
    private lateinit var binding: FragmentShareReportBinding
    private val groupShareViewModel: GroupShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GroupShareViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_report, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            vm = groupShareViewModel
            nextPage.setOnClickListener {
                startActivityForResult(
                    ShareEditStickerActivity.getIntent(requireContext(), viewToByteArray(imageContainer), groupShareViewModel.groupId),
                    ShareEditStickerActivity.KEY_SHARE
                )
            }
        }
    }

    private fun viewToByteArray(view: View): ByteArray {
        val bitmap = takeScreenShot(view)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        return stream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ShareEditStickerActivity.KEY_SHARE -> {
                if (resultCode == Activity.RESULT_OK) activity?.finish()
            }
        }
    }

    companion object {
        fun newInstance() = ShareReportFragment()
    }
}