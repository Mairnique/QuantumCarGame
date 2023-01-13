package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class FinishLineSprite extends RectF {
    int dX, color;
    int l, r, t, b;
    int currentBallY = 0;
    Bitmap finishLine;
    private Context mContext;

    public FinishLineSprite(float left, float top, float right, float bottom, int dX, int color, Bitmap b) {
        super(left, top, right, bottom);
        this.dX = dX;
        this.l = (int)left;
        this.r = (int)right;
        this.t = (int)top;
        this.b = (int)bottom;

        this.color = color;
        finishLine = b;
    }

    //  ********* MOVEMENT METHODS ********************
    public boolean updateOk(Canvas canvas) {
        if(left - dX <= 0){
            return false;
        }
        offset(dX, 0);
        return true;
    }

    public void drawFinishLine(Canvas canvas) {
        System.out.println("DREW DECOHERENCE");
        canvas.drawBitmap(finishLine, left, top, new Paint());
        Paint paint = new Paint();
        paint.setColor(color);
        int transparency = 255;
        paint.setARGB(transparency, 20, 200, 200);
        setColor(paint.getColor());
        canvas.drawRect(right, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    public void drawFinishLineScreen(Canvas canvas, int width, int height, Bitmap gameover, String finalTime) {
        canvas.drawBitmap(gameover, (int)(width/2- gameover.getWidth()/2), (int) (height/2 - gameover.getHeight()/2), new Paint());
        String finalTimeString = "Your score was: " + finalTime;
        Paint timeText = new Paint();
        timeText.setTextSize(50);
        canvas.drawText(finalTimeString, (float) width *6/8, 100, timeText);

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
