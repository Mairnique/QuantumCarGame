package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SuperpositionCar extends CarSprite {

    int pos;
    boolean inBounds;
    float top, bottom;

    public SuperpositionCar(CarSprite car, float dX, Bitmap bitmap, int pos) {
        super((int) car.left, (int) car.top, (int) car.right, (int) car.bottom, dX, 0, bitmap);
        top = car.top;
        bottom = car.bottom;
        this.pos = pos;
        inBounds = true;
    }

    public void update(RectF boundary) {
        if (this.right < boundary.left) {
            inBounds = false;
        }
        offset(dX, 0);
    }

}
