package kr.co.bigwalk.app.data.report.dto

import android.text.Html
import android.text.Spanned

data class CarbonEffectResponse(
    val carbonEffect: Long,
    val addedCarbonEffect: Long,
    val imagePath: String,
    private val description: String
) {
    fun getDescription(): Spanned {
        return Html.fromHtml(description)
    }
}
