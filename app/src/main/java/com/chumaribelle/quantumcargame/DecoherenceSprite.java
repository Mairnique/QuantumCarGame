package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class DecoherenceSprite extends RectF {
    int dX, dY, color;
    int l, r, t, b;
    int currentBallY = 0;
    Bitmap decoherenceImage;
    public DecoherenceSprite(float left, float top, float right, float bottom, int dX, int dY, int color, Bitmap b) {
        super(left, top, right, bottom);
        this.dX = dX;
        this.dY = dY;
        this.l = (int)left;
        this.r = (int)right;
        this.t = (int)top;
        this.b = (int)bottom;

        this.color = color;
        decoherenceImage = b;
    }

    public void update(Canvas canvas) {
        if(right+dX>canvas.getWidth()){
            dX*=-1;
        }
        if(left+dX<0){
            dX*=-1;
        }
        if(bottom+dY>canvas.getHeight()){
            dY*=-1;
        }
        if(top+dY<0){
            dY*=-1;
        }
        offset(dX,dY);
    }
    public void updateCircle(Canvas canvas){
        offset(0,dY);
    }

    public void drawCircle(Canvas canvas) {
        System.out.println("DREW CIRCLE");
        Paint paint = new Paint();
        int transparency = (int)(Math.random() * Math.random() * 10)+200;
        int red = (int)(Math.random() * (30) + 220);
        int green = (int)(Math.random() * (20) + 200);
        int blue = (int)(Math.random() * (30) + 1);
        paint.setARGB(transparency, red, green, blue);
//        paint.setColor(Color.BLUE);
        setColor(paint.getColor());
        canvas.drawCircle(centerX(), centerY(), width()/2, paint);
    }

    public void drawRectangle(Canvas canvas){
        Paint paint = new Paint();
        int transparency = (int)(Math.random() * Math.random() * 10)+200;
        int red = (int)(Math.random() * (30) + 220);
        int green = (int)(Math.random() * (20) + 200);
        int blue = (int)(Math.random() * (30) + 200);
        paint.setARGB(transparency, red, green, blue);
//        paint.setColor(Color.RED);
        setColor(paint.getColor());
        canvas.drawRect(left, top, right, bottom, paint);
    }
    public void drawConfetti(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        int radAdd = (int)(Math.random() * 5);
        int transparency = (int)(Math.random() * Math.random() * 155)+100;
        int red = (int)(Math.random() * (50) + 200);
        int green = (int)(Math.random() * (50) + 200);
        int blue = (int)(Math.random() * (50) + 200);
        paint.setARGB(transparency, red, green, blue);
//        paint.setColor(Color.WHITE);
        setColor(paint.getColor());
        canvas.drawCircle(centerX(),centerY(),width()/2+radAdd,paint);

    }

    public int getdX() {
        return dX;
    }

    public void setdX(int dX) {
        this.dX = dX;
    }

    public int getdY() {
        return dY;
    }

    public void setdY(int dY) {
        this.dY = dY;
    }

    public int getColor() {
        return color;
    }

    public void setL(int l) {
        left = l;
        this.l = l;
    }

    public void setR(int r) {
        right = r;
        this.r = r;
    }
    public void setT(int t) {
        top = t;
        this.t = t;
    }
    public void setB(int b) {
        bottom = b;
        this.b = b;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
