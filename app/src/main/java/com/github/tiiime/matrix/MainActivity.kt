package com.github.tiiime.matrix

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.github.tiiime.matrix.ktx.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    companion object {
        private const val MAX_SCALE = 2F
        private const val MIN_SCALE = 1F

    }

    val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var lastScaleEndScale = 1F

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            lastScaleEndScale = matrixView.circleMatrix[Matrix.MSCALE_X]
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            matrixView.updateScale(detector)
            val currentScale = matrixView.circleMatrix[Matrix.MSCALE_X]
            val targetScale = (lastScaleEndScale * detector.scaleFactor) / currentScale
            matrixView.circleMatrix.postScale(
                targetScale,
                targetScale,
                detector.focusX,
                detector.focusY
            )
            limitMatrixScale(matrixView.circleMatrix, detector.focusX, detector.focusY)
            ViewCompat.postInvalidateOnAnimation(matrixView)
            return super.onScale(detector)
        }
    }

    val detector by lazy { ScaleGestureDetector(this, scaleListener) }

    val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            matrixView.circleMatrix.postTranslate(-distanceX, -distanceY)
            limitMatrixTranslate(matrixView.circleMatrix)
            ViewCompat.postInvalidateOnAnimation(matrixView)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
    val scrollDetector by lazy { GestureDetector(this, scrollListener) }

    private fun limitMatrixScale(matrix: Matrix, privoX: Float, privoY: Float) {
        val scale = matrix[Matrix.MSCALE_X]

        val targetScale = min(max(MIN_SCALE, scale), MAX_SCALE) / scale
        matrix.postScale(targetScale, targetScale, privoX, privoY)
    }

    private fun limitMatrixTranslate(matrix: Matrix) {

        val rect = RectF(0F, 0F, matrixView.width.toFloat(), matrixView.height.toFloat())
        val rectBound = RectF(0F, 0F, matrixView.width.toFloat(), matrixView.height.toFloat())
        matrix.mapRect(rect)

        val offsetX =
            getOffset(rect.left, rect.right, rectBound.left, rectBound.right, rectBound.centerX())
        val offsetY =
            getOffset(rect.top, rect.bottom, rectBound.top, rectBound.bottom, rectBound.centerY())

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        matrixView.circleMatrix

        val list = initOperateList()
        var current = list.first()

        button.text = current.name
        button.setOnClickListener {
            current.run.run()
            current = current.next!!
            button.text = current.name
            ViewCompat.postInvalidateOnAnimation(matrixView)
        }

        matrixView.setOnTouchListener { v, event ->
            scrollDetector.onTouchEvent(event)
            detector.onTouchEvent(event)
        }
    }

    private fun initOperateList(): List<Action> {
        val postScale = Action("postScale", Runnable {
            matrixView.circleMatrix.postScale(
                1.5F,
                1.5F,
                matrixView.width / 2F,
                matrixView.height / 2F
            )
        })

        val setScale = Action("setScale", Runnable {
            matrixView.circleMatrix.setScale(
                1.5F,
                1.5F,
                matrixView.width / 2F,
                matrixView.height / 2F
            )
        })

        val preScale = Action("preScale", Runnable {
            matrixView.circleMatrix.preScale(
                1.5F,
                1.5F,
                matrixView.width / 2F,
                matrixView.height / 2F
            )
        })

        val translate = Action("translate", Runnable {
            matrixView.circleMatrix.postTranslate(100F, 100F)
        })
        val reset = Action("reset", Runnable {
            matrixView.circleMatrix.reset()
        })


        val list = listOf(
            translate.copy(), preScale.copy(), reset.copy(),
            translate.copy(), postScale.copy(), reset.copy(),
            translate.copy(), setScale.copy(), reset.copy()
        )


        list.fold(list.last(), { acc, action ->
            acc.next = action
            action
        })
        return list
    }


    data class Action(val name: String, val run: Runnable, var next: Action? = null)
}