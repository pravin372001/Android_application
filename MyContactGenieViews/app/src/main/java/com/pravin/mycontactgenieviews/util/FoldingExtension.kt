package com.pravin.mycontactgenieviews.util


import android.graphics.Rect
import android.view.View
import androidx.window.layout.FoldingFeature

fun FoldingFeature.isTableTopPosture(): Boolean {
    return state == FoldingFeature.State.HALF_OPENED &&
            orientation == FoldingFeature.Orientation.HORIZONTAL
}

fun FoldingFeature.isBookPosture(): Boolean {
    return state == FoldingFeature.State.HALF_OPENED &&
            orientation == FoldingFeature.Orientation.VERTICAL
}

fun FoldingFeature.isFlatPostureVertical(): Boolean {
    return state == FoldingFeature.State.FLAT &&
            orientation == FoldingFeature.Orientation.VERTICAL
}

fun FoldingFeature.isFlatPostureHorizontal(): Boolean {
    return state == FoldingFeature.State.FLAT &&
            orientation == FoldingFeature.Orientation.HORIZONTAL
}

fun getFeatureBoundsInWindow(foldingFeature: FoldingFeature, view: View): Rect? {
    val location = IntArray(2)
    view.getLocationInWindow(location)
    val viewLocationX = location[0]
    val viewLocationY = location[1]

    return if (foldingFeature.bounds.left >= viewLocationX &&
        foldingFeature.bounds.top >= viewLocationY &&
        foldingFeature.bounds.right <= viewLocationX + view.width &&
        foldingFeature.bounds.bottom <= viewLocationY + view.height) {
        foldingFeature.bounds
    } else {
        null
    }
}
