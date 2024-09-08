package kr.co.bigwalk.app.data.story.dto

data class Campaign(val id: Long?, val name: String?, val enabled: Boolean?)

data class Story(
    val id: Long,
    val title: String,
    val description: String,
    val storyContentType: StoryContentType,
    val storyContentImages: List<StoryContentImageResponse>,
    val videoPath: String,
    val reservationEnabled: Boolean,
    val thumbnailImagePath: String,
    val campaign: Campaign?
)