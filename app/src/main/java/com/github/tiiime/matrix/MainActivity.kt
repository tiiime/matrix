package com.github.tiiime.matrix

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.view.GestureDetector
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.github.tiiime.matrix.util.MatrixScaleGestureDetectorListener
import com.github.tiiime.matrix.util.MatrixScrollGestureDetectorListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val MAX_SCALE = 2F
        private const val MIN_SCALE = 1F

    }

    private val dragPinchMatrix = Matrix()

    private val scaleDetector by lazy {
        ScaleGestureDetector(
            this,
            MatrixScaleGestureDetectorListener(
                dragPinchMatrix,
                MIN_SCALE,
                MAX_SCALE,
                matrixUpdate = matrix_layout::updateMatrix
            )
        )
    }

    private val scrollDetector by lazy {
        GestureDetector(
            this, MatrixScrollGestureDetectorListener(
                matrix = dragPinchMatrix,
                limitRectF = RectF(
                    0F,
                    0F,
                    matrix_layout.width.toFloat(),
                    matrix_layout.height.toFloat()
                ),
                matrixUpdate = matrix_layout::updateMatrix
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val list = initOperateList()
//        var current = list.first()

//        button.text = current.name
//        button.setOnClickListener {
//            current.run.run()
//            current = current.next!!
//            button.text = current.name
//            ViewCompat.postInvalidateOnAnimation(matrixView)
//        }

//        matrixView.setOnTouchListener { v, event ->
//            scrollDetector.onTouchEvent(event)
//            scaleDetector.onTouchEvent(event)
//        }

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = object : RecyclerView.Adapter<TextHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder =
                TextHolder(this@MainActivity)

            override fun getItemCount(): Int = 100

            override fun onBindViewHolder(holder: TextHolder, position: Int) {
                (holder.itemView as TextView).text = "$position"
            }
        }

        list.setOnTouchListener { v, event ->
            scrollDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
            return@setOnTouchListener false
        }
    }

    class TextHolder(context: Context) : RecyclerView.ViewHolder(TextView(context))

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