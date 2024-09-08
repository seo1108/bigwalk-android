package kr.co.bigwalk.app.data.organization

import java.io.Serializable

data class Content(
    override val content: String
): ContentItem, Serializable
