package com.example.sklad_8.ui.util

import android.content.Context
import android.util.DisplayMetrics
import android.graphics.PointF
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class SpeedyLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(
        context, orientation, reverseLayout
    ) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
//        recyclerView.smoothSnapToPosition(position)
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            //This controls the direction in which smoothScroll looks
            //for your view
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@SpeedyLinearLayoutManager
                    .computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }

        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    companion object {
        const val MILLISECONDS_PER_INCH = 50f
    }
}