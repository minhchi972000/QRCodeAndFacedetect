package com.example.sampleface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class BarAndFaceBoxView(
    context: Context
) : View(context) {

    private val paint = Paint() //lưu trữ thông tin kiểu của hộp giới hạn

    private var mRect = RectF() //chứa tọa độ của hình chữ nhật sẽ được vẽ trên màn hình

    override fun onDraw(canvas: Canvas?) {  //hiển thị con số mong muốn trên màn hình mỗi khi setRect
        super.onDraw(canvas)

        val cornerRadius = 10f

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f

        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
    }

    fun setRect(rect: RectF) { //cập nhật giá trị của nó.
        mRect = rect
        invalidate()
        requestLayout()
    }
}