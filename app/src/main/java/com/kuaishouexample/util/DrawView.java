package com.kuaishouexample.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;


public class DrawView extends View {
    float left;
    float top;
    float right;
    float bottom;
    float[] lines;
    Point[] points;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, Point[] points) {
        super(context);
        this.lines = lines;
        this.points = points;
    }

    public DrawView(Context context, float left, float top, float right, float bottom) {
        super(context);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

    }

//    public void draw(EscherGraphics graphics) {
//        int[] px = {20, 70, 130, 240};
//        int[] py = {20, 150, 100, 130};
//        graphics.drawPolygon(px,py,4);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        p.setColor(Color.RED);// 设置红色
//        canvas.drawText("画矩形：", 10, 80, p);
        p.setColor(Color.GREEN);// 设置绿色
        p.setStyle(Paint.Style.STROKE);//设置填满
//        canvas.drawLines(lines, p);// 正方形
//        canvas.drawRect(1210, 351, 1290, 404, p);// 长方形
//        canvas.drawRect(left, top, right, bottom, p);// 长方形

        // 绘制路径
        Path path = new Path();
        path.moveTo(points[0].x, points[0].y);
        path.lineTo(points[1].x, points[1].y);
        path.lineTo(points[3].x, points[3].y);
        path.lineTo(points[2].x, points[2].y);
        path.close();// 封闭或者path.lineTo(400, 100);即开始的位置
        canvas.drawPath(path, p);

    }
}
