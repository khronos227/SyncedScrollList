package com.github.khronos227.syncedscrolllist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.khronos227.syncedscrolllist.extend.bindView
import com.github.khronos227.syncedscrolllist.view.ContentListView
import com.github.khronos227.syncedscrolllist.view.HeaderListView

class MainActivity : AppCompatActivity() {
    private val headerList: HeaderListView by this.bindView(R.id.header_list)
    private val contentList: ContentListView by this.bindView(R.id.content_list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.headerList.setOnSnapChangedCallback {
            this.contentList.smoothScrollToPosition(it)
        }
        this.contentList.setOnSnapChangedCallback {
            this.headerList.smoothScrollToPosition(it)
        }

        val urls = (100..200 step 10).map {
            "https://placehold.jp/${it}x$it.png"
        }

        this.headerList.update(urls)
        this.contentList.update(urls)
    }
}
