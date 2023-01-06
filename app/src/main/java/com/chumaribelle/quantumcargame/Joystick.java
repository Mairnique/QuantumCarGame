package com.chumaribelle.quantumcargame;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Joystick{

    private Paint innerPaint;
    private Paint outerPaint;
    private int outerCenterX;
    private int outerCenterY;
    private int innerCenterX;
    private int innerCenterY;
    private int outerRadius;
    private int innerRadius;
    private boolean mIsPressed;
    private double xPercent;
    private double yPercent;

    public Joystick(int cX, int cY, int or, int ir) {

        // Outer and inner circles
        outerCenterX = cX;
        outerCenterY = cY;
        innerCenterX = cX;
        innerCenterY = cY;

        // Radii of circles
        outerRadius = or;
        innerRadius = ir;

        // Paint for circles
        outerPaint = new Paint();
        outerPaint.setColor(Color.WHITE);
        outerPaint.setAlpha(70);
        outerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerPaint = new Paint();
        innerPaint.setColor(Color.BLUE);
        innerPaint.setAlpha(200);
        innerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(
                 outerCenterX,
                 outerCenterY,
                 outerRadius,
                outerPaint
        );

        canvas.drawCircle(
                 innerCenterX,
                 innerCenterY,
                 innerRadius,
                innerPaint
        );
    }

    public void update() {
        updateStick();
    }

    private void updateStick() {
        innerCenterX = (int) (outerCenterX + xPercent *outerRadius);
        innerCenterY = (int) (outerCenterY + yPercent *outerRadius);
    }

    public boolean isPressed(double x, double y) {
        double distanceToPress = Math.sqrt(Math.pow(x - outerCenterX, 2) + Math.pow(y-outerCenterY, 2));
        return distanceToPress < outerRadius;
    }

    public void setIsPressed(boolean b) {
        mIsPressed = b;
    }

    public boolean getIsPressed() {
        return mIsPressed;
    }

    public void setStick(double x, double y) {
        double xdist = x - innerCenterX;
        double ydist = y - outerCenterY;
        double distanceToPress = Math.sqrt(Math.pow(x - outerCenterX, 2) + Math.pow(y-outerCenterY, 2));

        if(distanceToPress < outerRadius) {
            xPercent = xdist/outerRadius;
            yPercent = ydist/outerRadius;
        }
        else {
            xPercent = xdist/distanceToPress;
            yPercent = ydist/distanceToPress;
        }
    }

    public void resetStick() {
        xPercent = yPercent = 0.0;
    }

    public double getXPercent() {
        return xPercent;
    }

    public double getYPercent() {
        return yPercent;
    }
}