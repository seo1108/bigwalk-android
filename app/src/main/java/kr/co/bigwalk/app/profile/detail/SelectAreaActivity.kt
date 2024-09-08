package kr.co.bigwalk.app.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.community.adapter.SelectAddress1DepthAdapter
import kr.co.bigwalk.app.community.adapter.SelectAddress2DepthAdapter
import kr.co.bigwalk.app.databinding.ActivitySelectAreaBinding
import kr.co.bigwalk.app.util.EventObserver
import java.io.Serializable

class SelectAreaActivity : BaseActivity<ActivitySelectAreaBinding>(R.layout.activity_select_area) {

    private val selectAreaViewModel by viewModels<ProfileSelectAreaViewModel>()

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

    private val address1Depth: String by lazy { intent.getStringExtra(KEY_ADDRESS_SIDO).orEmpty() }
    private val address2Depth: String by lazy { intent.getStringExtra(KEY_ADDRESS_SIGUNGU).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            exitButton.setOnClickListener {
                onBackPressed()
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
        with(selectAreaViewModel) {
            crewFirstAddressResponse.observe(this@SelectAreaActivity, Observer {
                selectAddress1DepthAdapter.submitList(it)
                selectAddress1DepthAdapter.setItemClick(address1Depth)
            })
            crewSecondAddressResponse.observe(this@SelectAreaActivity, Observer {
                selectAddress2DepthAdapter.run {
                    reselectPosition()
                    submitList(it)
                    selectAddress2DepthAdapter.setItemClick(this@SelectAreaActivity.address2Depth)
                }
            })
            saveSuccess.observe(this@SelectAreaActivity, EventObserver {
                val intent = intent.putExtra(KEY_SELECT_AREA, it as Serializable)
                setResult(RESULT_OK, intent)
                finish()
            })
        }
    }

    companion object {
        const val KEY_SELECT_AREA = "SELECT_AREA"
        private const val KEY_ADDRESS_SIDO = "ADDRESS_SIDO"
        private const val KEY_ADDRESS_SIGUNGU = "ADDRESS_SIGUNGU"
        fun getIntent(context: Context, address1Depth: String, address2Depth: String) =
            Intent(context, SelectAreaActivity::class.java).apply {
                putExtra(KEY_ADDRESS_SIDO, address1Depth)
                putExtra(KEY_ADDRESS_SIGUNGU, address2Depth)
            }
    }
}