package kr.co.bigwalk.app.feed.category

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.viewmodel.SingleLiveEvent
import kr.co.bigwalk.app.blame.BlameActivity
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.FeedCategory
import kr.co.bigwalk.app.databinding.FrameFeedByCategoryBinding
import kr.co.bigwalk.app.feed.*
import kr.co.bigwalk.app.feed.adapter.FeedChallengeRecyclerAdapter
import kr.co.bigwalk.app.feed.modify.ModifyFeedActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showAlertDialog


class FeedByCategoryFragment: Fragment() {

    companion object {
        const val CAMPAIGN_ID = "CAMPAIGN_ID"
        const val CATEGORY_TYPE = "CATEGORY_TYPE"
        const val CATEGORY_ID = "CATEGORY_ID"
        const val CATEGORY_NAME = "CATEGORY_NAME"
        const val IS_ENABLE_LIKE = "IS_ENABLE_LIKE"
        const val ORGANIZATION_ID = "ORGANIZATION_ID"
        const val USER_ID = "USER_ID"
        const val IS_MY_PAGE = "IS_MY_PAGE"
        const val SORT_TYPE = "SORT_TYPE"

        val feedChanged: MutableLiveData<Int> = MutableLiveData()
        //val getDataFinished: SingleLiveEvent<Boolean> = SingleLiveEvent()
        //val getDataFinished: MutableLiveData<Boolean> = SingleLiveEvent()
        val getDataFinished: MutableLiveData<Boolean> = SingleLiveEvent()
        //val onlyLoadOneItem: MutableLiveData<Boolean> = MutableLiveData()
        var SCROLL_TO_POSITION_LOADED = false
        var scrollToMove: Boolean = false
        var isLoading: Boolean = false
        var topAppend: Boolean = false
        var bottomAppend: Boolean = false
        var appendDirection = ""
        var isFinalDataLoad = false
        var totalCount = 0

        var isFirstLoad: Boolean = true
        var firstLoadCount = 0
        //var upPageNum: Int = 0
        //var downPageNum: Int = 0

        /*fun scrollToCurrent() {
            DebugLog.d("department=0_______________scrollToCurrent")
            Handler().postDelayed(Runnable {
                recyclerView.scrollToPosition(size)
            }, 500)
        }*/

        fun newInstance(
            navigator: BaseNavigator,
            campaignId: Long,
            category: FeedCategory,
            isEnableLike: Boolean,
            organizationId: Long,
            sortType: String,
            userId: Long = -1,
            isMyPage: Boolean = false
        ): FeedByCategoryFragment {
            val bundle = Bundle()
            bundle.putLong(CAMPAIGN_ID, campaignId)
            bundle.putInt(CATEGORY_TYPE, category.type.value)
            bundle.putLong(CATEGORY_ID, category.id)
            bundle.putString(CATEGORY_NAME, category.name)
            bundle.putBoolean(IS_ENABLE_LIKE, isEnableLike)
            bundle.putLong(ORGANIZATION_ID, organizationId)
            bundle.putLong(USER_ID, userId)
            bundle.putBoolean(IS_MY_PAGE, isMyPage)
            bundle.putString(SORT_TYPE, sortType)

            val fragment = FeedByCategoryFragment()
            fragment.arguments = bundle
            fragment.navigator = navigator
            return fragment
        }
    }

    private lateinit var navigator: BaseNavigator
    private lateinit var viewModel: FeedByCategoryViewModel
    private lateinit var adapter: FeedChallengeRecyclerAdapter
    private lateinit var binding: FrameFeedByCategoryBinding
    private lateinit var factory: FeedByCategoryViewModelFactory



    private var isEnableLike = false
    private var organizationId = -1L
    private var isMyPage = false
    private var sortType = ""
    private var departmentId = -1L

    private var selectedPage = 0
    private var upPageNum = 0
    private var downPageNum = 0
    private var size = 0
    private var positionToMove = 0

    private var scrollDirection = "down"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frame_feed_by_category, container, false)
        val bundle: Bundle = arguments!!
        binding.lifecycleOwner = this

        isEnableLike = bundle.getBoolean(IS_ENABLE_LIKE)
        organizationId = bundle.getLong(ORGANIZATION_ID)
        isMyPage = bundle.getBoolean(IS_MY_PAGE)
        sortType = bundle.getString(SORT_TYPE).toString()
        departmentId = bundle.getLong(CATEGORY_ID)

        getDataFinished.value = false
        scrollDirection = "down"
        SCROLL_TO_POSITION_LOADED = false
        isFinalDataLoad = false
        appendDirection = "bottom"

        DebugLog.d("selectableDepartments 2= $departmentId")

        if (departmentId == -1L) {
            selectedPage = PreferenceManager.getSelectedPage()
            upPageNum = PreferenceManager.getSelectedPage() - 1
            downPageNum = PreferenceManager.getSelectedPage() + 1
            size = PreferenceManager.getSelectedSize()
            positionToMove = PreferenceManager.getSelectedPosition()
        } else {
            selectedPage = 0
            upPageNum = -1
            downPageNum = 1
            size = PreferenceManager.getSelectedSize()
            positionToMove = 0
        }



        factory = FeedByCategoryViewModelFactory(activity as? BaseNavigator,
                                                 bundle.getLong(CAMPAIGN_ID),
                                                 FeedViewModel.getFeedCategoryType(bundle.getInt(CATEGORY_TYPE)),
                                                 bundle.getLong(CATEGORY_ID),
                                                 bundle.getLong(USER_ID))

        viewModel = ViewModelProvider(this, factory).get(FeedByCategoryViewModel::class.java)

        viewModel.feedList.clear()
        when (sortType) {
            "hot" -> {
                viewModel!!.requestHotFeedList(PreferenceManager.getSelectedChallengeId(), PreferenceManager.getSelectedImageId(), departmentId, selectedPage, size)
            }
            "recent" -> {
                viewModel!!.requestRecentFeedList(PreferenceManager.getSelectedChallengeId(), PreferenceManager.getSelectedImageId(), departmentId, selectedPage, size)
            }
            "my" -> {
                viewModel!!.requestMyFeedList(PreferenceManager.getSelectedChallengeId(), PreferenceManager.getSelectedImageId(), departmentId, selectedPage, size)
            }
        }

        binding.viewModel = viewModel

        var recyclerView = binding.feedChallengeRecycler
        adapter = FeedChallengeRecyclerAdapter(viewModel, isEnableLike, organizationId, isMyPage)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null

        binding.feedChallengeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    scrollDirection = "down"
                } else if (dy < 0) {
                    //Scrolling up
                    scrollDirection = "up"
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                DebugLog.d("department=0 onScrollStateChanged detected")

                val lastVisibleItemPosition =(binding.feedChallengeRecycler.layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()
                val totalCount = binding.feedChallengeRecycler.adapter!!.itemCount-1

                DebugLog.d("department=0 onScrollStateChanged detected $lastVisibleItemPosition $totalCount")

                if (!recyclerView.canScrollVertically(1)) {
                    // 순차 재생
                    DebugLog.d("requestHitFeedList loadSequentialData $departmentId")
                    loadSequentialData()
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    // 함수 호출 중에는 재호출 방지 로직 필요
                    // 역순 재생
                    DebugLog.d("requestHitFeedList loadReverseData $departmentId")
                    loadReverseData()
                }
            }
        })






        FeedManager.dimFeed.observe(viewLifecycleOwner, Observer {
            adapter.dimFeed(it)
        })
        viewModel.data.observe(viewLifecycleOwner, Observer {
            adapter.dimFeed(it)
        })
        viewModel.clickMoreButton.observe(viewLifecycleOwner, Observer { feed ->
            val dialog = FeedOptionDialogFragment.newInstance(feed.mine, object : FeedOptionDialogFragment.OptionClickListener {
                override fun onBlameClick() {
                    startActivity(BlameActivity.getIntent(requireContext(), feed.actionDonationHistoryId, feed.user.id, BlameType.FEED))
                }

                override fun onModifyClick() {
                    PreferenceManager.saveFeedInfo(feed.actionDonationHistoryId, feed.likeCount, feed.liked, feed.commentCount, feed.comment!!, false)
                    val list = arrayListOf<String>()
                    list.add(feed.certifiedImagePath)
                    feed.certifiedImagePath2?.let { list.add(it) }
                    feed.certifiedImagePath3?.let { list.add(it) }
                    startActivity(ModifyFeedActivity.getIntent(requireContext(), list, feed.comment.orEmpty(), feed.actionDonationHistoryId))
                }

                override fun onDeleteClick() {
                    showAlertDialog(requireContext(), R.string.dialog_msg_feed_delete) {
                        viewModel.requestFeedDelete(feed.actionDonationHistoryId)
                        FirebaseAnalytics.getInstance(requireContext()).logEvent("feed_btn_contents_delete_click", Bundle())
                        Handler(Looper.getMainLooper()).postDelayed({
                            adapter.refreshFeed()
                        },2000)
                    }
                }

            })
            dialog.show(childFragmentManager, dialog.tag)
        })
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.emptyMission.isVisible = itemCount == 0
            }
        })

        binding.refreshLayout.setOnRefreshListener {
            initData()
        }

        getDataFinished.observe(viewLifecycleOwner, Observer {
            DebugLog.d("SCROLL_TO_POSITION_LOADED loadMoreOneItem observeOnce ${PreferenceManager.getSelectedPosition()%size} ${PreferenceManager.getFeedTotalCount()} $scrollToMove ${viewModel.loadMoreOneItem.value} $departmentId")
            DebugLog.d("SCROLL_TO_POSITION_LOADED scrollToCurrent smoothScroller pre ${PreferenceManager.getSelectedPosition()%size} ${PreferenceManager.getFeedTotalCount()} ${PreferenceManager.getSelectedPosition()} ${viewModel.loadMoreOneItem.value} $SCROLL_TO_POSITION_LOADED")
            if (PreferenceManager.getSelectedPosition() > 0
                && departmentId == -1L
                && !SCROLL_TO_POSITION_LOADED
                && viewModel.loadMoreOneItem.value != false
                && PreferenceManager.getFeedTotalCount() != PreferenceManager.getSelectedPosition()+1) {
                DebugLog.d("SCROLL_TO_POSITION_LOADED scrollToCurrent smoothScroller start1")
                firstMoveToPosition()
            }

            if (PreferenceManager.getSelectedPosition() > 0
                && departmentId == -1L
                && !SCROLL_TO_POSITION_LOADED
                && viewModel.loadMoreOneItem.value != false
                && PreferenceManager.getFeedTotalCount() == PreferenceManager.getSelectedPosition()+1
                && PreferenceManager.getSelectedPosition() % size > 0) {
                DebugLog.d("SCROLL_TO_POSITION_LOADED scrollToCurrent smoothScroller start2")
                firstMoveToPosition()
            }

            if (departmentId == -1L
                && getDataFinished.value == true
                && viewModel.loadMoreOneItem.value == false
                && PreferenceManager.getSelectedPage() > 0
                && PreferenceManager.getSelectedPosition()%size == 0) {
                DebugLog.d("SCROLL_TO_POSITION_LOADED loadMoreOneItem observeOnce1 ${SCROLL_TO_POSITION_LOADED} ${viewModel.loadMoreOneItem.value} $departmentId")
                DebugLog.d("SCROLL_TO_POSITION_LOADED loadMoreOneItem observeOnce2 ${viewModel.loadMoreOneItem.value} $departmentId")
                scrollDirection = "up"
                appendDirection = "top"
                SCROLL_TO_POSITION_LOADED = true
                viewModel.loadMoreOneItem.value = true
                DebugLog.d("loadReverseData onlyLoadOneItem $departmentId")
                loadReverseData()
                scrollToCurrent()
            }

            if (getDataFinished.value == true && scrollToMove) {
                DebugLog.d("SCROLL_TO_POSITION_LOADED GO scrollToCurrent $departmentId")
                //scrollToMove = false
                scrollToCurrent()
            }
        })

        return binding.root
    }


    private fun loadSequentialData() {
        if (isFinalDataLoad) return
        if (isLoading) return
        appendDirection = "bottom"
        isLoading = true
        // 순차 재생

        scrollToMove = false
        topAppend = false
        bottomAppend = true

        when (sortType) {
            "hot" -> {
                viewModel!!.requestHotFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    downPageNum,
                    size
                )
            }
            "recent" -> {
                viewModel!!.requestRecentFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    downPageNum,
                    size
                )
            }
            "my" -> {
                viewModel!!.requestMyFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    downPageNum,
                    size
                )
            }
        }


        downPageNum++

    }

    private fun loadReverseData() {
        // 함수 호출 중에는 재호출 방지 로직 필요
        // 역순 재생
        DebugLog.d("loadReverseData pre $departmentId $appendDirection ${PreferenceManager.getSelectedPage()} $scrollDirection $SCROLL_TO_POSITION_LOADED $upPageNum $isLoading" )
        if (scrollDirection == "down") return
        //if (!SCROLL_TO_POSITION_LOADED) return
        if (upPageNum < 0 || isLoading) return

        isLoading = true
        appendDirection = "top"

        DebugLog.d("loadReverseData start $departmentId $appendDirection ${PreferenceManager.getSelectedPage()} $scrollDirection $SCROLL_TO_POSITION_LOADED $upPageNum $isLoading" )

        scrollToMove = true
        topAppend = true
        bottomAppend = false


        when (sortType) {
            "hot" -> {
                viewModel!!.requestHotFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    upPageNum,
                    size
                )
            }
            "recent" -> {
                viewModel!!.requestRecentFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    upPageNum,
                    size
                )
            }
            "my" -> {
                viewModel!!.requestMyFeedList(
                    PreferenceManager.getSelectedChallengeId(),
                    PreferenceManager.getSelectedImageId(),
                    departmentId,
                    upPageNum,
                    size
                )
            }
        }


        upPageNum--
        firstLoadCount++
    }

    private fun firstMoveToPosition() {




        DebugLog.d("SCROLL_TO_POSITION_LOADED scrollToCurrent smoothScroller after")
        /*val smoothScroller: RecyclerView.SmoothScroller by lazy {
            object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference() = SNAP_TO_START
            }
        }
        var pos = positionToMove%size
        smoothScroller.targetPosition = pos
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            binding.feedChallengeRecycler.layoutManager?.startSmoothScroll(smoothScroller)
            binding.feedChallengeRecycler.scrollBy(0, 20)
        }, 500)*/

        val smoothScroller: RecyclerView.SmoothScroller by lazy {
            object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference() = SNAP_TO_START
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return 5f / displayMetrics!!.densityDpi
                }
            }
        }

        smoothScroller.targetPosition = positionToMove%size
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            binding.feedChallengeRecycler.layoutManager?.startSmoothScroll(smoothScroller)
            binding.feedChallengeRecycler.scrollBy(0, 20)
        }, 500)

        SCROLL_TO_POSITION_LOADED = true
    }

    private fun scrollToCurrent() {
        //DebugLog.d("SCROLL_TO_POSITION_LOADED scrollToCurrent $size")
/*        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            binding.feedChallengeRecycler.smoothSnapToPosition(size, SNAP_TO_START)
        }, 3000)*/
        /*val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            val smoothScroller: RecyclerView.SmoothScroller by lazy {
                object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference() = SNAP_TO_START
                    *//*override fun getHorizontalSnapPreference() = SNAP_TO_START *//*
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                        return 5f / displayMetrics!!.densityDpi
                    }
                }
            }

            smoothScroller.targetPosition = size
            binding.feedChallengeRecycler.layoutManager?.startSmoothScroll(smoothScroller)
        }, 2000)*/

        /*val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            //binding.feedChallengeRecycler.smoothScrollToPosition(10)
            binding.feedChallengeRecycler.layoutManager?.startSmoothScroll(smoothScroller)
        }, 3000)*/

        /*val smoothScroller: RecyclerView.SmoothScroller by lazy {
            object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference() = SNAP_TO_START
                *//*override fun getHorizontalSnapPreference() = SNAP_TO_START *//*
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return 3f / displayMetrics!!.densityDpi
                }
            }
        }

        smoothScroller.targetPosition = size
        binding.feedChallengeRecycler.layoutManager?.startSmoothScroll(smoothScroller)*/
        binding.feedChallengeRecycler.scrollToPosition(size)
    }

    private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val scrollDuration = 0.1f;
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return scrollDuration / computeVerticalScrollRange();
            }
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun onResume() {
        super.onResume()
        DebugLog.d("like feed request resume ${departmentId}")

        if (departmentId > 0) {
            PreferenceManager.saveSelectedFeedInfo(PreferenceManager.getSelectedChallengeId(), -1, 0,
                kr.co.bigwalk.app.feed.ChallengeDetailActivity.PAGE_SIZE, 0)
        }

        if (PreferenceManager.isFeedUpload()) {
            PreferenceManager.saveFeedUpload(false, PreferenceManager.addedFeedId())
            initData()
        } else {
            var refreshPosition: Int = -1
            var isAddedItem = true

            if (viewModel.feedList.isNotEmpty()) {

                run breaker@{
                    viewModel.feedList.forEachIndexed() { idx, it ->
                        DebugLog.d("like feed request resume list ${PreferenceManager.addedFeedId()} ${PreferenceManager.getFeedId()} $it")
                        if (it.actionDonationHistoryId == PreferenceManager.getFeedId()) {
                            refreshPosition = idx
                        }
                        DebugLog.d("isAddedItem ${it.actionDonationHistoryId} ${PreferenceManager.addedFeedId()} ${PreferenceManager.getFeedId()}")

                        if (it.actionDonationHistoryId == PreferenceManager.addedFeedId()) {
                            DebugLog.d("isAddedItem false")
                            isAddedItem = false
                            PreferenceManager.saveFeedUpload(false, PreferenceManager.addedFeedId())
                            return@breaker
                        }
                    }
                }

                DebugLog.d("isAddedItem $isAddedItem")

                if (PreferenceManager.addedFeedId() > 0 && isAddedItem) {
                    initData()
                } else {
                    adapter.refreshFeed(refreshPosition)
                }
            }
        }
    }

    private fun initData() {
        selectedPage = 0
        upPageNum = -1
        downPageNum = 1
        positionToMove = 0
        scrollDirection = "down"
        SCROLL_TO_POSITION_LOADED = true
        isFinalDataLoad = false
        scrollToMove = false
        firstLoadCount = 0
        viewModel.loadMoreOneItem.value = false

        viewModel.feedList.clear()
        adapter.notifyDataSetChanged()

        when (sortType) {
            "hot" -> {
                viewModel!!.requestHotFeedList(PreferenceManager.getSelectedChallengeId(), -1, departmentId, 0, size)
            }
            "recent" -> {
                viewModel!!.requestRecentFeedList(PreferenceManager.getSelectedChallengeId(), -1, departmentId, 0, size)
            }
            "my" -> {
                viewModel!!.requestMyFeedList(PreferenceManager.getSelectedChallengeId(), -1, departmentId, 0, size)
            }
        }

        binding.refreshLayout.isRefreshing = false
    }

    private fun <T> MutableLiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}

