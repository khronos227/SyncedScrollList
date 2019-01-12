package com.github.khronos227.syncedscrolllist.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class CenterZoomLayoutManager : CenterLayoutManager {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) :
            super(context, orientation, reverseLayout)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        this.setChildScale()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (this.orientation != HORIZONTAL) return 0

        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        this.setChildScale()

        return scrolled
    }

    private val shrinkDistance = 0.8F
    private val shrinkAmount = 0.2F
    private fun setChildScale() {
        val midPoint = this.width / 2F
        val d0 = 0F
        val d1 = this.shrinkDistance * midPoint
        val s0 = 1F
        val s1 = 1F - this.shrinkAmount
        (0 until this.childCount).forEach {
            val child = this.getChildAt(it) ?: return
            val childMidPoint = (child.left + child.right) / 2F
            val d = min(d1, abs(midPoint - childMidPoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            child.scaleX = scale
            child.scaleY = scale
        }
    }
}
