package com.github.khronos227.syncedscrolllist.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.khronos227.syncedscrolllist.R
import com.github.khronos227.syncedscrolllist.extend.bindView
import com.github.khronos227.syncedscrolllist.util.GlideApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class ContentListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private var dragging = false

    init {
        val layoutManager = CenterLayoutManager(context, RecyclerView.HORIZONTAL, false)
        this.layoutManager = layoutManager
        this.adapter = ContentListAdapter()
        this.addItemDecoration(ContentItemDecoration())

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(this)
        this.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    // AbsListView.OnScrollListener.SCROLL_STATE_FLING -> println("fling $newState")
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        this@ContentListView.dragging = true
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        this@ContentListView.dragging = false
                        this@ContentListView.currentSnapPosition.takeIf { it != RecyclerView.NO_POSITION }?.let {
                            this@ContentListView.snapChangedCallback?.invoke(it)
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(layoutManager) ?: return
                val newPosition = layoutManager.getPosition(snapView)
                if (this@ContentListView.dragging && this@ContentListView.currentSnapPosition != newPosition) {
                    this@ContentListView.snapChangedCallback?.invoke(newPosition)
                }
            }
        })
    }

    fun update(list: List<String>) = GlobalScope.launch {
        (this@ContentListView.adapter as ContentListAdapter).update(list)
    }

    private var currentSnapPosition = RecyclerView.NO_POSITION
    private var snapChangedCallback: ((Int) -> Unit)? = null
    fun setOnSnapChangedCallback(callback: ((Int) -> Unit)?) {
        this.snapChangedCallback = callback
    }
}

internal class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ViewHolder>() {
    private var list: List<String> = emptyList()
    override fun getItemCount() = this.list.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.list.getOrNull(position)?.let { holder.bindData(it) }
    }

    fun update(list: List<String>) {
        this@ContentListAdapter.list = list
        this@ContentListAdapter.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView by this.itemView.bindView(R.id.content_image)

        fun bindData(url: String) {
            GlideApp.with(this.itemView.context)
                .load(url)
                .centerCrop()
                .into(this.image)
        }
    }
}

internal class ContentItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        outRect.left = if (position == 0) {
            (parent.width - parent.height * 3 / 5) / 2
        } else {
            16
        }
        outRect.right = if (position + 1 == parent.adapter?.itemCount) {
            (parent.width - parent.height * 3 / 5) / 2
        } else {
            16
        }
    }
}