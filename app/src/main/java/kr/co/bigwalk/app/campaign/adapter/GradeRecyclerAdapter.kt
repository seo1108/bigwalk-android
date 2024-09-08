package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.GradeIcon
import kr.co.bigwalk.app.data.campaign.dto.RankGuide
import kr.co.bigwalk.app.databinding.ItemRecyclerGradeInfoBinding
import kr.co.bigwalk.app.loadGradeIcon
import kr.co.bigwalk.app.util.longValueToCommaString

class GradeRecyclerAdapter(
    private val category: String
    ): RecyclerView.Adapter<GradeRecyclerAdapter.GradeViewHolder>() {
    val items = ArrayList<RankGuide>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        return GradeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_grade_info,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        holder.bind(category, items[position], position, itemCount-1)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class GradeViewHolder(
        private val binding: ItemRecyclerGradeInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, rankGuide: RankGuide, position: Int, maxPosition: Int) {
            loadGradeIcon(binding.gradeIcon, GradeIcon(category, rankGuide.level))
            binding.gradeLevel.text = rankGuide.getLevelText()
            binding.gradeName.text = rankGuide.rank

            val res = binding.root.context.resources
            var format: String
            when (position) {
                0 -> {
                    format = res.getString(R.string.grade_info_achievement_condition_less)
                    binding.gradeDescription.text = String.format(format, longValueToCommaString(rankGuide.endPoint))
                }
                maxPosition -> {
                    format = res.getString(R.string.grade_info_achievement_condition_more)
                    binding.gradeDescription.text = String.format(format, longValueToCommaString(rankGuide.startPoint))
                }
                else -> {
                    format = res.getString(R.string.grade_info_achievement_condition_more_less)
                    binding.gradeDescription.text = String.format(
                        format,
                        longValueToCommaString(rankGuide.startPoint),
                        longValueToCommaString(rankGuide.endPoint)
                    )
                }
            }
        }
    }
}