package kr.co.bigwalk.app.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecoration(private var divHeight: Int, private val isHorizontal: Boolean = false) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount!! - 1) {
            if (isHorizontal) {
                outRect.right += divHeight
            } else {
                outRect.bottom = divHeight
            }
        }
    }

}