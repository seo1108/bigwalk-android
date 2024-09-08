package kr.co.bigwalk.app.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kr.co.bigwalk.app.community.CommunityInfoActivity
import kr.co.bigwalk.app.community.share.GroupShareActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.dialog.SignalCallback
import kr.co.bigwalk.app.dialog.SignalImageDialog
import kr.co.bigwalk.app.dialog.SignalTextDialog
import kr.co.bigwalk.app.dialog.SignalViewModel

open class SignalBaseActivity : AppCompatActivity() {
    private val signalViewModel: SignalViewModel by lazy {
        ViewModelProvider(this).get(SignalViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindLiveData()
    }

    private fun bindLiveData() {
        with(signalViewModel) {
            imageSignal.observe(this@SignalBaseActivity, Observer {
                SignalImageDialog(this@SignalBaseActivity, it, object : SignalCallback {
                    override fun onConfirm() {
                        startActivity(GroupShareActivity.getIntent(this@SignalBaseActivity, PreferenceManager.getGroupId()))
                    }

                }).show()
            })
            textSignal.observe(this@SignalBaseActivity, Observer {
                SignalTextDialog(this@SignalBaseActivity, it).show()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        signalViewModel.requestSignal()
    }
}