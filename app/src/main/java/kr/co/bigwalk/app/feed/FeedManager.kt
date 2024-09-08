package kr.co.bigwalk.app.feed

import androidx.lifecycle.MutableLiveData
import kr.co.bigwalk.app.data.feed.dto.Feed

object FeedManager {
    val updateFeed = MutableLiveData<Triple<Int, Feed, Int>>()
    val removeFeed = MutableLiveData<Long>()
    val commentFeed = MutableLiveData<Feed>()
    val dimFeed = MutableLiveData<Long>()
    val blockUserId = MutableLiveData<Long>()
}