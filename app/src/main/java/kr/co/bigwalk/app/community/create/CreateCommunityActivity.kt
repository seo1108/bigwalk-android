package kr.co.bigwalk.app.community.create

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.CommunityInfoActivity
import kr.co.bigwalk.app.databinding.ActivityCreateCommunityBinding
import kr.co.bigwalk.app.util.EventObserver

class CreateCommunityActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateCommunityBinding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(this).get(CreateCommunityViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_community)
        binding.lifecycleOwner = this

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.create_crew_container, CreateCrew1Fragment.newInstance())
            .commit()

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = createCommunityViewModel
            exitButton.setOnClickListener {
                onBackPressed()
            }
            nextButton.setOnClickListener {
                showNextFragment()
            }
        }
    }

    private fun bindViewModel() {
        with(createCommunityViewModel) {
            createProgressValue.observe(this@CreateCommunityActivity, Observer {
                ObjectAnimator.ofInt(binding.progress, "progress", it)
                    .setDuration(300)
                    .start()
            })
            registerSuccessEvent.observe(this@CreateCommunityActivity, EventObserver { crewId ->
                startActivity(CommunityInfoActivity.getIntent(this@CreateCommunityActivity, crewId))
                finish()
            })
        }
    }

    private fun showNextFragment() {
        var nextFragment: Fragment = CreateCrew1Fragment.newInstance()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.create_crew_container)
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            when(currentFragment) {
                is CreateCrew1Fragment -> { nextFragment = CreateCrew2Fragment.newInstance()}
                is CreateCrew2Fragment -> { nextFragment = CreateCrew3PreviewFragment.newInstance()}
                is CreateCrew3PreviewFragment -> {
                    createCommunityViewModel.registerCrew()
                    return
                }
            }
            transaction
                .setCustomAnimations(R.anim.anim_horizon_enter, R.anim.anim_horizon_exit2, R.anim.anim_horizon_enter2, R.anim.anim_horizon_exit)
                .hide(currentFragment)
        }
        transaction
            .setCustomAnimations(R.anim.anim_horizon_enter, R.anim.anim_horizon_exit2, R.anim.anim_horizon_enter2, R.anim.anim_horizon_exit)
            .add(R.id.create_crew_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.create_crew_container)
        if (currentFragment != null) {
            when (currentFragment) {
                is CreateCrew1Fragment -> {
                    createCommunityViewModel.setFunctionAndViewForScreen(0)
                }
                is CreateCrew2Fragment -> {
                    createCommunityViewModel.setFunctionAndViewForScreen(1)
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, CreateCommunityActivity::class.java)
    }
}