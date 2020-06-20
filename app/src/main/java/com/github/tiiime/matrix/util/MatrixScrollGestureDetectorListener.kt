package com.github.tiiime.matrix.util

import android.graphics.Matrix
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent

class MatrixScrollGestureDetectorListener(val matrix: Matrix,val limitRectF: RectF,val matrixUpdate:(Matrix)->Unit): GestureDetector.SimpleOnGestureListener() {

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        matrix.postTranslate(-distanceX, -distanceY)
        limitMatrixTranslate(matrix)
        matrixUpdate(matrix)
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    private fun limitMatrixTranslate(matrix: Matrix) {
        val matrixRect = RectF(limitRectF)
        val boundRect = RectF(limitRectF)
        matrix.mapRect(matrixRect)

        val offsetX =
            getOffset(matrixRect.left, matrixRect.right, boundRect.left, boundRect.right, boundRect.centerX())
        val offsetY =
            getOffset(matrixRect.top, matrixRect.bottom, boundRect.top, boundRect.bottom, boundRect.centerY())

        if (offsetX != 0F || offsetY != 0F) {
            matrix.postTranslate(offsetX, offsetY)
        }
    }

    /**
     * copy from
     * https://github.com/facebook/fresco/blob/23a0d0f66f75a21aa76817c1c2170fe25ce8bed9/samples/zoomable/src/main/java/com/facebook/samples/zoomable/DefaultZoomableController.java#L507
     */
    private fun getOffset(
        matrixStart: Float,
        matrixEnd: Float,
        limitStart: Float,
        limitEnd: Float,
        limitCenter: Float
    ): Float {
        val imageWidth = matrixEnd - matrixStart
        val limitWidth = limitEnd - limitStart
        val limitInnerWidth = (limitCenter - limitStart).coerceAtMost(limitEnd - limitCenter) * 2;
        // center if smaller than limitInnerWidth
        if (imageWidth < limitInnerWidth) {
            return limitCenter - (matrixEnd + matrixStart) / 2;
        }
        // to the edge if in between and limitCenter is not (limitLeft + limitRight) / 2
        if (imageWidth < limitWidth) {
            return if (limitCenter < (limitStart + limitEnd) / 2) {
                limitStart - matrixStart;
            } else {
                limitEnd - matrixEnd;
            }
        }
        // to the edge if larger than limitWidth and empty space visible
        if (matrixStart > limitStart) {
            return limitStart - matrixStart;
        }
        if (matrixEnd < limitEnd) {
            return limitEnd - matrixEnd;
        }
        return 0F
    }
}