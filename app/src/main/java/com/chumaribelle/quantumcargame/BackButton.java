package com.chumaribelle.quantumcargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BackButton
{
    Bitmap back;
    // fields
    public BackButton(Bitmap inputBack) {
        back = inputBack;
    }

    public void draw (Canvas canvas) {
        canvas.drawBitmap(back, 0, 0, new Paint());
    }



}
