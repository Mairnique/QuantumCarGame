package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SuperpositionCar extends CarSprite {

    int pos;
    boolean inBounds;

    public SuperpositionCar(int l, int t, int r, int b, float dX, Bitmap bitmap, int pos) {
        super(l, t, r, b, dX, 0, bitmap);
        this.pos = pos;
    }

    public void update(RectF boundary) {
        if (this.right < boundary.left) {
            inBounds = false;
        }
        offset(dX, 0);
    }

}
