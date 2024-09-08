package kr.co.bigwalk.app.sign_in.character

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.databinding.ActivitySelectCharacterBinding
import kr.co.bigwalk.app.databinding.ItemViewPagerCharacterBinding
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.my_page.MyPageModifyActivity
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import java.io.Serializable

class SelectCharacterActivity: AppCompatActivity() {

    private var selectedCharacter: Int = 0
    private var signUpUserRequest: Serializable? = null
    private var allSetCharacter: Boolean = false

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("select_character_view", Bundle())
        signUpUserRequest = intent.getSerializableExtra("SignUpUserRequest")// 회원가입 과정
        allSetCharacter = intent.getBooleanExtra("AllSetCharacter", false)// 전체메뉴 - 캐릭터 설정
        val binding: ActivitySelectCharacterBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_character)
        binding.activity = this
        binding.selectCharacterViewpager.adapter = ViewPagerAdapter()
        binding.selectCharacterViewpager.offscreenPageLimit = 3
        val pageMargin = 12f
        val pageOffset = 12f
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        var mOffset = 0F
        binding.selectCharacterViewpager.setPageTransformer { page, position ->
            if (metrics.widthPixels <= 720 && metrics.heightPixels <= 1199) mOffset = position * -(2F * pageOffset + pageMargin)
            else if ((metrics.widthPixels > 720 || metrics.widthPixels <= 1080) && (metrics.heightPixels > 1199 || metrics.heightPixels <= 2064))
                mOffset = 0F
            else mOffset = position * -(pageOffset + pageMargin)
            when {
                position <= 1 -> {
                    page.translationX = mOffset
                }
            }
        }

        binding.selectCharacterViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {// character_id 는 1부터 시작.
                    0 -> selectedCharacter = position + 1
                    1 -> selectedCharacter = position + 1
                    2 -> selectedCharacter = position + 1
                    3 -> selectedCharacter = position + 1
                    4 -> selectedCharacter = position + 1
                }
            }
        })
    }

    fun moveToNext() {
        if (allSetCharacter) {// 전체 메뉴 - 캐릭터 설정 일 때
            setCharacter()
        } else {
            PreferenceManager.saveCharacter("$selectedCharacter")
            if (signUpUserRequest != null && signUpUserRequest is SignUpUserRequest) {// 회원가입 과정일 때
                (signUpUserRequest as SignUpUserRequest).characterId = selectedCharacter
                (signUpUserRequest as SignUpUserRequest).profileType = ProfileType.CHARACTER.type
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("SignUpUserRequest", signUpUserRequest)
                startActivity(intent)
            } else if (intent.getStringExtra("CHARACTER_PROFILE") == "CHARACTER_PROFILE") {// 회원가입에서 캐릭터 선택 후 프로필 수정에서 다시 캐릭터 선택할 때
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("ProfilePath", "$selectedCharacter")
                setResult(RESULT_OK, intent)
            } else {// 개인정보 수정 과정일 때
                val intent = Intent(this, MyPageModifyActivity::class.java)
                intent.putExtra("CharacterId", selectedCharacter)
                setResult(RESULT_OK, intent)
            }
        }

        finish()
    }

    fun setCharacter() {
        UserRepository.setCharacter(
            selectedCharacter,
            object : UserDataSource.SetCharacterCallback {
                override fun onSuccess(response: UserProfileResponse) {
                    PreferenceManager.saveCharacter("${response.characterId}")
                    showToast("캐릭터를 변경하였습니다.")
                }

                override fun onFailed(reason: String) {
                    showToast("캐릭터를 변경할 수 없습니다. 다시 시도해주세요!")
                    DebugLog.e(UserProfileException(reason))
                }

            })
    }

    class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

        private val characters: List<Int> = listOf(R.drawable.select_bigker, R.drawable.select_worry, R.drawable.select_sol, R.drawable.select_dami, R.drawable.select_ollie_buddy)

        class ViewHolder(private val binding: ItemViewPagerCharacterBinding): RecyclerView.ViewHolder(binding.root) {

            fun bind(character: Int) {
                binding.drawable = character
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder (
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_view_pager_character,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return characters.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(characters[position])
        }

    }

}