package com.github.tiiime.matrix.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

class MatrixLayout : FrameLayout {
    private val transformMatrix = Matrix()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.setMatrix(transformMatrix)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    fun updateMatrix(matrix: Matrix){
        this.transformMatrix.set(matrix)
        ViewCompat.postInvalidateOnAnimation(this)
    }
}