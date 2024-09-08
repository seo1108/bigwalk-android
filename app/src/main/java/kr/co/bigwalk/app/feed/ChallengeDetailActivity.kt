package kr.co.bigwalk.app.feed

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_challenge_detail.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityChallengeDetailBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.feed.adapter.ChallengeImageAdapter
import kr.co.bigwalk.app.feed.adapter.FeedChallengeRecyclerAdapter
import kr.co.bigwalk.app.util.DebugLog


class ChallengeDetailActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityChallengeDetailBinding
    private var viewModel: ChallengeDetailViewModel? = null
    private var imageType: String = ""
    private var challengeId: Long = 0
    private var page = 0
   // private var size = 15
    private var scrollToY = 450
    private var screenGap = 135

    private lateinit var madapter: ChallengeImageAdapter

    companion object {
        const val PAGE_SIZE = 15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var screenWidth = displayMetrics.widthPixels
        var screenHeight = displayMetrics.heightPixels

        challengeId = intent.getLongExtra("challengeId", 0)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_detail)
        viewModel = ChallengeDetailViewModel(this)

        madapter = ChallengeImageAdapter(viewModel!!, screenWidth/3,this)

        binding.mainScrollView.run {
            header = header_view
            stickListener = { _ ->
                Log.d("LOGGER_TAG", "stickListener")
            }
            /*freeListener = { _ ->
                Log.d("LOGGER_TAG", "freeListener")
            }*/
        }

        viewModel!!.isPopular.set(true)

        // 챌린지 상세 정보
        viewModel!!.requestChallengeDetail(challengeId)

        if (viewModel!!.isPopular.get() == true) {
            imageType = ImageType.HIT.value

        } else if (viewModel!!.isRecent.get() == true) {
            imageType = ImageType.RECENT.value

        } else if (viewModel!!.isMy.get() == true) {
            imageType = ImageType.MY.value

        }

        // 이미지 리스트
        viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)


        binding.viewModel = viewModel


        val boldTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.nanum_square_bold)
        val regularTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.nanum_square_regular)



        binding.challengeDetailRecycler.minimumHeight = screenHeight-(screenGap.dpToPx())

        binding.popularContainer.setOnClickListener {
            if (!viewModel!!.isPopular.get()!!) {
                viewModel!!.isPopular.set(true)
                viewModel!!.isRecent.set(false)
                viewModel!!.isMy.set(false)

                binding.popularTitle.typeface = boldTypeface
                binding.recentTitle.typeface = regularTypeface
                binding.myTitle.typeface = regularTypeface

                imageType = ImageType.HIT.value

                viewModel!!.challengeImageList.clear()
                page = 0
                viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
                /*Handler().postDelayed(
                    Runnable {
                        binding.mainScrollView.smoothScrollTo(0,scrollToY.dpToPx())
                    },
                    100
                )*/

            }
        }

        binding.recentContainer.setOnClickListener {
            if (!viewModel!!.isRecent.get()!!) {
                viewModel!!.isPopular.set(false)
                viewModel!!.isRecent.set(true)
                viewModel!!.isMy.set(false)

                binding.popularTitle.typeface = regularTypeface
                binding.recentTitle.typeface = boldTypeface
                binding.myTitle.typeface = regularTypeface

                imageType = ImageType.RECENT.value

                viewModel!!.challengeImageList.clear()
                page = 0
                viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
                /*Handler().postDelayed(
                    Runnable {
                        binding.mainScrollView.smoothScrollTo(0,scrollToY.dpToPx())
                    },
                    100
                )*/
            }
        }



        binding.myContainer.setOnClickListener {
            if (!viewModel!!.isMy.get()!!) {
                viewModel!!.isPopular.set(false)
                viewModel!!.isRecent.set(false)
                viewModel!!.isMy.set(true)

                binding.popularTitle.typeface = regularTypeface
                binding.recentTitle.typeface = regularTypeface
                binding.myTitle.typeface = boldTypeface

                imageType = ImageType.MY.value

                viewModel!!.challengeImageList.clear()
                page = 0
                viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
                PreferenceManager.saveFeedUpload(false, -1)
                /*Handler().postDelayed(
                    Runnable {
                        binding.mainScrollView.smoothScrollTo(0,scrollToY.dpToPx())
                        binding.challengeDetailRecycler.smoothScrollToPosition(0)
                             },
                    100
                )*/
            }
        }

        binding.mainScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.mainScrollView.getChildAt(0).bottom
                <= binding.mainScrollView.height + binding.mainScrollView.scrollY
            ) {

                // Scroll Bottom
                if (viewModel!!.isLoadFinish.get() == false) {
                    page++
                    viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
                }
            }
        }

        binding.ivFeedDetailBack.setOnClickListener {
            finish()
        }

        binding.ivFeedDetailPlay.setOnClickListener {
            val dialog = viewModel!!.challengeDetail.get()!!.campaignId?.let { it1 ->
                FeedPhotoSelectFragment.newInstance(it1)
            }
            dialog!!.show(supportFragmentManager, "FeedPhotoSelectFragment")
        }

        //binding.challengeDetailRecycler.adapter = ChallengeImageAdapter(viewModel!!, this)
        binding.challengeDetailRecycler.isNestedScrollingEnabled = false
        binding.challengeDetailRecycler.adapter = madapter

        /*binding.challengeDetailRecycler.setHasFixedSize(true)
        binding.challengeDetailRecycler.itemAnimator = null*/
        /*binding.challengeDetailRecycler.setNestedScrollingEnabled(false)*/
    }


    override fun getContext(): Activity {
        return this
    }

    override fun onResume() {
        super.onResume()
        DebugLog.d("challenge detail resume start")
        if (PreferenceManager.isFeedUpload()) {
            DebugLog.d("challenge detail resume isFeedUpload")
            PreferenceManager.saveFeedUpload(false, PreferenceManager.addedFeedId())
            viewModel!!.challengeImageList.clear()
            page = 0
            viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
        } else {
            DebugLog.d("challenge detail resume not isFeedUpload")
            var isAddedItem = true
            DebugLog.d("challenge detail resume parsing ${PreferenceManager.addedFeedId()}")
            if (viewModel!!.feedList.isNotEmpty()) {
                run breaker@ {
                    viewModel!!.feedList.forEachIndexed() { idx, it ->
                        DebugLog.d("challenge detail resume list ${PreferenceManager.addedFeedId()} ${PreferenceManager.getFeedId()} $it")
                        if (it.actionDonationHistoryId == PreferenceManager.addedFeedId()) {
                            isAddedItem = false
                            //PreferenceManager.saveFeedUpload(false, -1)
                            return@breaker
                        }
                    }
                }
                DebugLog.d("challenge detail resume list $isAddedItem")
                if (PreferenceManager.addedFeedId() > 0 && isAddedItem) {
                    viewModel!!.challengeImageList.clear()
                    page = 0
                    viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
                }
            }
        }

        /*DebugLog.d("challenge detail resume start")
        if (PreferenceManager.isFeedUpload()) {
            PreferenceManager.saveFeedUpload(false)
            viewModel!!.challengeImageList.clear()
            page = 0
            viewModel!!.requestChallengeImage(challengeId, imageType, page, PAGE_SIZE)
        }

        if (PreferenceManager.getFeedId() < 0) return
        var position: Int = -1
        run {
            viewModel!!.feedList.forEachIndexed() { idx, it ->
                DebugLog.d("challenge detail resume list ${PreferenceManager.getFeedId()} $it")
                if (it.actionDonationHistoryId == PreferenceManager.getFeedId()) {
                    position = idx
                    return@run
                }
            }
        }
        DebugLog.d("challenge detail resume list $position")
        if (position < 0) return
        if (viewModel!!.feedList.isNotEmpty()) {
            if (PreferenceManager.getFeedId() == -1L) return
            if (PreferenceManager.getFeedIsDelete()) {
                viewModel!!.feedList.removeAt(position)
                madapter!!.notifyItemRemoved(position)
            }
        }*/




    }

    enum class ImageType (
        val value: String
    ) {
        HIT("hit"), RECENT("recent"), MY("my");
    }
}