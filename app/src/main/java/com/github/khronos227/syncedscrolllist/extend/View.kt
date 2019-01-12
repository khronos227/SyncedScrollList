package com.github.khronos227.syncedscrolllist.extend

import android.view.View
import androidx.annotation.IdRes

inline fun <reified T> View.bindView(@IdRes id: Int) = lazy { this.findViewById<View>(id) as T }