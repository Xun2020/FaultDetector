package com.BUAARSE.faultdetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShowWaveView extends View {
//      波形图画笔
    private Paint mWavePaint;
//    坐标轴
    private Paint xoyPaint;
//      波形图路径
    private Path mPath;
//      波形数据
    private ArrayList waveDate = new ArrayList();
//    目前的坐标
    private float nowX,nowY;
//    构造函数
    public ShowWaveView(Context context){
        super(context);
    }
    public ShowWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ShowWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
//初始化函数
    private void init(){
        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mWavePaint.setStrokeCap(Paint.Cap.ROUND);

        mWavePaint .setColor(Color.parseColor("#fd4a4a"));//颜色
        mWavePaint.setAntiAlias(true);//抗锯齿效果
        mWavePaint.setStrokeWidth(3);//波线宽

        xoyPaint = new Paint();
        xoyPaint.setStyle(Paint.Style.STROKE);
        xoyPaint.setStrokeWidth(5);//网格线宽
        xoyPaint.setColor(Color.parseColor("#202020"));//颜色
        xoyPaint.setAntiAlias(true);//抗锯齿效果

        mPath = new Path();
    }

    private float mWidth,mHeight;//自身大小

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
//        int[] colors = {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.MAGENTA};
//        float[] position = {0f,0.2f,0.4f,0.6f,0.8f,1.0f};
        Shader shader = new LinearGradient(mWidth/2,0,mWidth/2,mHeight/2,
                new int[]{Color.RED,Color.GREEN,Color.BLUE},new float[]{0.4f,0.7f,0.99f},Shader.TileMode.MIRROR);
        mWavePaint.setShader(shader);
        xoyPaint.setShader(shader);
    }

    private int xNUM;//网格行列数

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();//获取view的宽
        mHeight = getMeasuredHeight();//获取view的高
        xNUM = (int) (mWidth/8);//获取行数
    }

    //画坐标轴
    private void drawXOY(Canvas canvas){
        canvas.drawLine(0,mHeight/2, mWidth, mHeight/2, xoyPaint);
        canvas.drawLine(mWidth/2,45, mWidth/2, mHeight-90, xoyPaint);
    }

    //画波形图,中间开始画，左移
    private void drawWaveLine(Canvas canvas) {
        if (null == waveDate || waveDate.size() <= 0) {
            return;
        }
        mPath.reset();
        mPath.moveTo(mWidth, mHeight / 2);
        int j = 0;
        for (int i = waveDate.size() - 1; i >= 0; i--) {
            nowX = mWidth - j*8;
            j++;
            float dataValue = (float) waveDate.get(i);
            nowY = mHeight/2-dataValue * (mHeight/120);
            if(nowY<45){
                nowY = 45;
            }
            if(nowY>mHeight/2){
                nowY = mHeight/2;
            }
            mPath.moveTo(nowX, nowY);
            mPath.lineTo(nowX,mHeight-nowY);
        }
        canvas.drawPath(mPath, mWavePaint);
        if (waveDate.size() > xNUM ) {
            waveDate.remove(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制波形
        drawWaveLine(canvas);
        drawXOY(canvas);
    }

    public void showLine(float line) {
//        添加单元
        waveDate.add(line);
        //刷新函数
        postInvalidate();
    }
    public void cleanPaint(){
        waveDate.clear();
        postInvalidate();
    }
}