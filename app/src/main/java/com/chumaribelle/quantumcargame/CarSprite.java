package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class CarSprite extends RectF {
    private static final double MAX_PIXELS_PER_SECOND = 400.0;
    private static final double MAX_SPEED = MAX_PIXELS_PER_SECOND / GameView.MAX_UPS;
    int dX, dY;
    int left, top, right, bottom;
    Bitmap carImg;

    public CarSprite(int l, int t, int r, int b, int dX, int dY, Bitmap bitmap) {
        super(l,t,r,b);
        carImg = bitmap;
        this.dX = dX;
        this.dY = dY;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(carImg, null, this, new Paint());
    }

    public void update(Joystick joystick, RectF boundary) {
        dX = (int) (joystick.getXPercent()*MAX_SPEED);
        dY = (int) (joystick.getYPercent()*MAX_SPEED);
        RectF test = new RectF(this);
        test.offset(dX, dY);
        if(boundary.contains(test)) {
            offset(dX, dY);
        }
    }
}
