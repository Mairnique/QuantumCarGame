package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class DecoherenceSprite extends RectF {
    int dX, color;
    int l, r, t, b;
    int currentBallY = 0;
    Bitmap decoherenceImage;
    private Context mContext;

    public DecoherenceSprite(float left, float top, float right, float bottom, int dX, int color, Bitmap b) {
        super(left, top, right, bottom);
        this.dX = dX;
        this.l = (int)left;
        this.r = (int)right;
        this.t = (int)top;
        this.b = (int)bottom;

        this.color = color;
        decoherenceImage = b;
    }

    //  ********* MOVEMENT METHODS ********************
    public boolean updateOk(Canvas canvas) {
            if(left + dX <= 0){
                return false;
            }
            offset(dX, 0);
        return true;
    }

    public void drawDecoherence(Canvas canvas) {
        canvas.drawBitmap(decoherenceImage, left,top, new Paint());
    }

    public void drawDecoherenceWhiteScreen(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        int radAdd = (int)(Math.random() * 5);
        int transparency = (int)(Math.random() * Math.random() * 155)+100;
        int red = (int)(Math.random() * (30) + 222);
        int green = (int)(Math.random() * (30) + 222);
        int blue = (int)(Math.random() * (30) + 222);
        paint.setARGB(transparency, red, green, blue);
        setColor(paint.getColor());
        canvas.drawRect(0, 0, width(), height(), paint);
    }

    // ********* GETTER AND SETTER METHODS ********************

    public int getdX() {
        return dX;
    }

    public void setdX(int dX) {
        this.dX = dX;
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
