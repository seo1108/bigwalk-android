package kr.co.bigwalk.app.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ItemRecyclerBottomSheetBinding
import kr.co.bigwalk.app.report.ReportActivity
import kr.co.bigwalk.app.report.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

class BottomSheetRecyclerAdapter(private val viewModel: ReportViewModel): RecyclerView.Adapter<BottomSheetRecyclerAdapter.BottomSheetViewHolder>() {

    private var items: List<String> = listOf()

    class BottomSheetViewHolder(private val binding: ItemRecyclerBottomSheetBinding, private val viewModel: ReportViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: String, position: Int) {
            binding.listElement = listItem
//            itemView.setOnClickListener {
//                if (viewModel.isSelectMonth.get()) {
//                    val simpleDateFormat = SimpleDateFormat("yyyy.MM", Locale.KOREA)
//                    viewModel.selectedMonth.set(MonthElement(simpleDateFormat.parse(listItem)))
//                    viewModel.selectedWeek.set(WeekElement(viewModel.selectedMonth.get(), 0))
//                } else {
//                    viewModel.selectedWeek.set(WeekElement(viewModel.selectedMonth.get(), position))
//                }
//
//                if (viewModel.selectedWeek.get()?.weekOfMonth == 0) { // 월단위 기록
//                    viewModel.getMonthlyStatistics(listItem)
//                } else { // 주간 기록
//                    viewModel.getWeeklyStatistics()
//                }
//                viewModel.finishBottomSheet()
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_bottom_sheet,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    fun setItems(listItems: List<String>) {
        this.items = listItems
        notifyDataSetChanged()
    }
}