package kr.co.bigwalk.app.data.report.dto

import android.text.Html
import android.text.Spanned

data class EnergyEffectResponse(
    val energyEffect: Long,
    val addedEnergyEffect: Long,
    val imagePath: String,
    private val description: String
) {
    fun getDescription(): Spanned {
        return Html.fromHtml(description)
    }
}