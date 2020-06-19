package com.github.tiiime.matrix

import android.graphics.Matrix
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.github.tiiime.matrix.ktx.get
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
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
            ViewCompat.postInvalidateOnAnimation(matrixView)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
    val scrollDetector by lazy { GestureDetector(this, scrollListener) }

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