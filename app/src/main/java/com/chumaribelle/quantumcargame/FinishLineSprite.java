package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageButton;

public class FinishLineSprite extends RectF {
    int dX, color;
    int l, r, t, b;
    int currentBallY = 0;
    Bitmap finishLine;
    Bitmap backButton;
    private Context mContext;

    public FinishLineSprite(float left, float top, float right, float bottom, int dX, int color, Bitmap b, Bitmap back) {
        super(left, top, right, bottom);
        this.dX = dX;
        this.l = (int)left;
        this.r = (int)right;
        this.t = (int)top;
        this.b = (int)bottom;

        this.color = color;
        finishLine = b;
        backButton = back;
    }

    //  ********* MOVEMENT METHODS ********************
//    public boolean updateOk(Canvas canvas) {
//
//        if(left - dX <= 0){
//            return false;
//        }
//        offset(dX, 0);
//        System.out.println("Sprite Left: " + left + "; Top: " + top);
//        canvas.drawRect(left,top,100,100, new Paint());
//        return true;
//
////        while (left + dX > 0) {
////            offset(dX, 0);
////            System.out.println("Left: " + left + "; Top: " + top);
////            Paint paint = new Paint(Color.BLUE);
////            canvas.drawBitmap(finishLine, left, top, paint);
////            canvas.drawRect(0, 0, 100, 100, new Paint());
////            return true;
////        }
////        return false;
//    }
//
//    public void drawFinishLine(Canvas canvas, int screenWidth, int screenHeight) {
//        System.out.println("DREW FINISH LINE-------------------------------------");
////        for (int i = 0; i < 100)
//        canvas.drawBitmap(finishLine, left, top, new Paint());
//        Paint paint = new Paint();
//        int transparency = 255;
//        for (int i = 0; i < 255; i++) {
//            paint.setARGB(i, 20, 200, 200);
//            setColor(paint.getColor());
//            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
//        }
//    }
//
    public void drawFinText(Canvas canvas, int width, int height, Bitmap gameover, String finalTime, Paint text) {
        canvas.drawBitmap(gameover, (int)(width/2- gameover.getWidth()/2), (int) (height/2 - gameover.getHeight()/2), new Paint());
        // score text
        String finalTimeString = "Your time was: " + finalTime;
        text.setTextSize(100);
        canvas.drawText(finalTimeString, (float) width *2/8, 100, text);
//        canvas.drawBitmap(backButton, 0, 0, new Paint());
    }

    public boolean updateOk(Canvas canvas) {
        if(left - dX < 0){
            return false;
        }
        offset(dX, 0);
        return true;
    }

    public void drawFinishLine(Canvas canvas) {
        System.out.println("DREW DECOHERENCE");
        canvas.drawBitmap(finishLine, left,top, new Paint());
        Paint r = new Paint();
        r.setARGB(255,40,100,100);
        canvas.drawRect(left + finishLine.getWidth(), 0, canvas.getWidth(), canvas.getHeight(), r);
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
