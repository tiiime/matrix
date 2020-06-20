package com.github.tiiime.matrix.util

import android.graphics.Matrix
import android.view.ScaleGestureDetector
import com.github.tiiime.matrix.ktx.get
import kotlin.math.max
import kotlin.math.min

class MatrixScaleGestureDetectorListener(
    private val matrix: Matrix,
    private val minScale: Float,
    private val maxScale: Float,
    private val matrixUpdate: (Matrix) -> Unit
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    private var lastScaleEndScale = 1F

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        super.onScaleEnd(detector)
        lastScaleEndScale = matrix[Matrix.MSCALE_X]
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val currentScale = matrix[Matrix.MSCALE_X]
        val targetScale = (lastScaleEndScale * detector.scaleFactor) / currentScale
        matrix.postScale(
            targetScale,
            targetScale,
            detector.focusX,
            detector.focusY
        )
        limitMatrixScale(matrix, detector.focusX, detector.focusY)
        matrixUpdate(matrix)
        return super.onScale(detector)
    }

    private fun limitMatrixScale(matrix: Matrix, privoX: Float, privoY: Float) {
        val scale = matrix[Matrix.MSCALE_X]

        val targetScale = min(max(minScale, scale), maxScale) / scale
        matrix.postScale(targetScale, targetScale, privoX, privoY)
    }
}