package com.github.tiiime.matrix.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.view.View

class MatrixView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val circleMatrix = Matrix()

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val paint = Paint().apply {
        color = Color.BLACK
    }

    private val redPaint = Paint().apply {
        color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(width / 2F, 0F, width / 2F, height.toFloat(), paint)
        canvas.drawLine(0F, height / 2F, width.toFloat(), height / 2F, paint)

        drawCircle(canvas)

        scaleDetector?.let {
            drawScale(
                canvas,
                it.focusX,
                it.focusY
            )
        }
    }

    private fun drawScale(canvas: Canvas, focusX: Float, focusY: Float) {
        canvas.drawCircle(focusX, focusY, 10F, redPaint)
        canvas.drawText("focus", focusX + 10, focusY + 10, redPaint)
    }


    private fun drawCircle(canvas: Canvas) {
        canvas.save()
        canvas.setMatrix(circleMatrix)
        canvas.drawCircle(width / 2F, height / 2F, 200F, paint)

        canvas.drawLine(width / 2F, 0F, width / 2F, height.toFloat(), redPaint)
        canvas.drawLine(0F, height / 2F, width.toFloat(), height / 2F, redPaint)

        canvas.restore()
    }

    private var scaleDetector: ScaleGestureDetector? = null
    fun updateScale(detector: ScaleGestureDetector) {
        scaleDetector = detector
    }


}