package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class CarSprite extends RectF {
    private static final double MAX_PIXELS_PER_SECOND = 400.0;
    private static final double MAX_SPEED = MAX_PIXELS_PER_SECOND / GameView.MAX_UPS;
    float dX, dY;
    Bitmap carImg;

    public CarSprite(int l, int t, int r, int b, float dX, float dY, Bitmap bitmap) {
        super(l,t,r,b);
        carImg = bitmap;
        this.dX = dX;
        this.dY = dY;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(carImg, null, this, new Paint());
    }

    public void update(Joystick joystick, RectF boundary) {
        if (joystick.getIsPressed()) {
            dX = (float) (joystick.getXPercent() * MAX_SPEED);
            dY = (float) (joystick.getYPercent() * MAX_SPEED);
        }
        else {

            dX = (float) (dX>0 ? Math.max(dX - 0.25, 0): Math.min(dX+0.25, 0));
            dY = (float) (dY>0 ? Math.max(dY - 0.25, 0): Math.min(dY+0.25, 0));
        }
        if (this.left+dX > boundary.left && this.right+dX < boundary.right) {
            offset(dX, 0);
        }
        if (this.top+dY > boundary.top && this.bottom+dY<boundary.bottom) {
            offset(0, dY);
        }
    }
}
