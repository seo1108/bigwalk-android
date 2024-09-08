package kr.co.bigwalk.app.story.reserved

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityMyReservedStoriesBinding
import kr.co.bigwalk.app.story.StoryViewModel
import kr.co.bigwalk.app.story.adapter.StoryRecyclerAdapter

class MyReservedStoryActivity: AppCompatActivity(), BaseNavigator {

    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMyReservedStoriesBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_reserved_stories)
        viewModel = StoryViewModel(this)
        binding.viewModel = viewModel
        val adapter = StoryRecyclerAdapter(lifecycle, viewModel)
        binding.myReservedStoryRecycler.adapter = adapter
        viewModel.myReservedStories.observe(this,
            Observer { pagedList -> adapter.submitList(pagedList) })

    }

    override fun getContext(): Activity {
        return this
    }

}