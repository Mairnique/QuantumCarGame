package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;


public class ProbabilitySprite extends RectF{
    int dX, color;
    int l, r, t, b;
    int currentBallY = 0;
    Bitmap probabilityImage;
    private Context mContext;

    public ProbabilitySprite(float left, float top, float right, float bottom, int dX, int color, Bitmap b) {
        super(left, top, right, bottom);
        this.dX = dX;
        this.l = (int)left;
        this.r = (int)right;
        this.t = (int)top;
        this.b = (int)bottom;

        this.color = color;
        probabilityImage = b;
    }

    //  ********* MOVEMENT METHODS ********************
    public boolean updateOk(Canvas canvas) {
        if(left - dX <= 0){
            return false;
        }
        offset(dX, 0);
        return true;
    }

    public void drawProbability(Canvas canvas) {
        canvas.drawBitmap(probabilityImage, left,top, new Paint());
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
