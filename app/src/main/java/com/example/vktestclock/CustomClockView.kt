package com.example.vktestclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var centerX = 0.0f
    private var centerY = 0.0f
    private var radius = 0.0f
    private var borderColor = 0
    private var fillColor = 0
    private var divisionColor = 0
    private var secondHandColor = 0
    private var minuteHandColor = 0
    private var hourHandColor = 0
    private var numberColor = 0
    private var timeZone: String? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomClockView, defStyleAttr, 0)
        borderColor = typedArray.getColor(R.styleable.CustomClockView_borderColor, Color.BLACK)
        fillColor = typedArray.getColor(R.styleable.CustomClockView_fillColor, Color.WHITE)
        divisionColor = typedArray.getColor(R.styleable.CustomClockView_divisionColor, Color.BLACK)
        secondHandColor = typedArray.getColor(R.styleable.CustomClockView_secondHandColor, Color.RED)
        minuteHandColor = typedArray.getColor(R.styleable.CustomClockView_minuteHandColor, Color.DKGRAY)
        hourHandColor = typedArray.getColor(R.styleable.CustomClockView_hourHandColor, Color.BLACK)
        numberColor = typedArray.getColor(R.styleable.CustomClockView_numberColor, Color.BLACK)
        timeZone = typedArray.getString(R.styleable.CustomClockView_timeZone)
        typedArray.recycle()
    }

    private val numberPaint = Paint().apply {
        color = numberColor
        style = Paint.Style.FILL
        textSize = 45f
        textAlign = Paint.Align.CENTER
    }

    private val borderPaint = Paint().apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val fillPaint = Paint().apply {
        color = fillColor
        style = Paint.Style.FILL
    }

    private val divisionPaint = Paint().apply {
        color = divisionColor
        style = Paint.Style.STROKE
    }

    private val secondHandPaint = Paint().apply {
        color = secondHandColor
        style = Paint.Style.FILL
        strokeWidth = 4f
    }

    private val minuteHandPaint = Paint().apply {
        color = minuteHandColor
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val hourHandPaint = Paint().apply {
        color = hourHandColor
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = (min(w, h) / 2f) * 0.9f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClock(canvas)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putString("timeZone", timeZone)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        timeZone = bundle.getString("timeZone")
        val instanceState = if (SDK_INT >= 33) {
            bundle.getParcelable("instanceState", Parcelable::class.java)
        } else {
            @Suppress("DEPRECATION") bundle.getParcelable("instanceState")
        }
        super.onRestoreInstanceState(instanceState)
    }

    fun setTimeZone(value: String) {
        timeZone = value
    }

    private fun drawClock(canvas: Canvas) {
        drawScale(canvas)
        drawNumbers(canvas)
        drawHands(canvas)
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        timeZone?.let {
            calendar.timeZone = TimeZone.getTimeZone(it)
        }
        val second = calendar.get(Calendar.SECOND)
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR)
        drawHourHand(canvas, hour)
        drawMinuteHand(canvas, minute)
        drawSecondHand(canvas, second)
        postInvalidateDelayed(1000)
    }

    private fun drawHourHand(canvas: Canvas, hour: Int) {
        val angle = Math.PI / 6 * hour - Math.PI/2
        drawHand(canvas, radius * 0.6f, angle, hourHandPaint)
    }

    private fun drawMinuteHand(canvas: Canvas, minute: Int) {
        val angle = Math.PI / 30 * minute - Math.PI/2
        drawHand(canvas, radius * 0.8f, angle, minuteHandPaint)
    }

    private fun drawSecondHand(canvas: Canvas, second: Int) {
        canvas.drawCircle(centerX, centerY, radius * 0.05f, secondHandPaint)
        val angle = Math.PI / 30 * second - Math.PI/2
        drawHand(canvas, radius * 0.8f, angle, secondHandPaint)
    }

    private fun drawHand(canvas: Canvas, len: Float, angle: Double, paint: Paint) {
        val startX = centerX
        val startY = centerY
        val stopX = centerX + len * cos(angle).toFloat()
        val stopY = centerY + len * sin(angle).toFloat()
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    private fun drawScale(canvas: Canvas) {
        centerX = width / 2f
        centerY = height / 2f
        radius = (min(width, height) / 2f) * 0.9f
        canvas.drawCircle(centerX, centerY, radius, fillPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        var len: Float
        for (i in 0 until 60) {
            if (i % 5 == 0) {
                divisionPaint.strokeWidth = 20f
                len = radius * 0.15f
            } else {
                divisionPaint.strokeWidth = 10f
                len = radius * 0.05f
            }
            val angle = Math.PI / 30 * i
            val startX = centerX + (radius - len) * cos(angle).toFloat()
            val startY = centerY + (radius - len) * sin(angle).toFloat()
            val stopX = centerX + radius * cos(angle).toFloat()
            val stopY = centerY + radius * sin(angle).toFloat()
            canvas.drawLine(startX, startY, stopX, stopY, divisionPaint
            )
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        for (hour in 1..12) {
            val angle = Math.PI / 6 * hour - Math.PI/2
            val startX = centerX + (radius * 0.7f) * cos(angle).toFloat()
            val startY = centerY + (radius * 0.7f) * sin(angle).toFloat()
            canvas.drawText(hour.toString(), startX, startY, numberPaint)
        }
    }
}