package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{

    public static final double MAX_UPS = 30.0;

    private SurfaceHolder mSurfaceHolder;
    Context mContext;
    private int mViewWidth;
    private int mViewHeight;
    private CarSprite mCar;
    private boolean mRunning;
    private Thread mGameThread;
    private Paint mPaint;
    private Path mPath;
    private Joystick mJoystick;
    private RectF mBoundary;
    private DecoherenceSprite mDecoherence;
    private int position;
    private int lastDecoPosition;
    private Bitmap decoBitmap;
    private ArrayList<DecoherenceSprite> decoArray;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mSurfaceHolder = getHolder();
    }

    public void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();
        setFocusable(true);
        mPaint = new Paint();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        Bitmap carBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.racecar);
        carBitmap = Bitmap.createScaledBitmap(carBitmap, (int) (mViewWidth/8.5), (int) (mViewHeight/10), false);
        mCar = new CarSprite(20, (mViewHeight/2)-(carBitmap.getHeight()/2), 20+carBitmap.getWidth(),
                (mViewHeight/2)+(carBitmap.getHeight()/2), 0, 0, carBitmap);
        mJoystick = new Joystick(200, mViewHeight-200, 150, 40);
        mBoundary = new RectF(0,
                mViewHeight/7,
                mViewWidth,
                mViewHeight*6/7);

        // Decoherence Setup
        decoBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.decoherence);
        decoArray = new ArrayList<>();
    }


    public void pause() {
        mRunning = false;
        try {
            // Stop the thread (rejoin the main thread)
            mGameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mJoystick = new Joystick((int) event.getX(), (int) event.getY(), 120, 60);
                if(mJoystick.isPressed(event.getX(), event.getY())) {
                    mJoystick.setIsPressed(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(mJoystick.getIsPressed()) {
                    mJoystick.setStick(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                mJoystick.setIsPressed(false);
                mJoystick.resetStick();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void drawBackground(Canvas canvas) {
        // Grass
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(0,0,mViewWidth,(float) mViewHeight/7, mPaint);
        canvas.drawRect(0, (float) mViewHeight*6/7, mViewWidth, mViewHeight, mPaint);

        // Road
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(0, (float) mViewHeight/7, mViewWidth, (float) mViewHeight*6/7, mPaint);

        // Lane Markers
        mPaint.setColor(Color.WHITE);
        for(int i=2; i<6; i++) {
            float top = (float) mViewHeight/7*i-5;
            float bottom = top+10;
            for(int j=0; j<=10; j++) {
                float left = (float) mViewWidth/10*j + (float) mViewWidth/20;
                float right = left+25;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    @Override
    public void run() {
        // Variables
        Canvas canvas;
        long frameStartTime;
        long frameTime;
        final int FPS = 60;
        int decoRand = (int)(Math.random() * 10);
        int prevPosition = position;

        // Running stuff
        System.out.println("POS2 " + position);
        while(mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                this.position += 5; // update position

                // record start time for run
                frameStartTime = System.nanoTime();

                canvas = mSurfaceHolder.lockCanvas();
                canvas.save();

                drawBackground(canvas);

                mCar.draw(canvas);

                mCar.update(mJoystick, mBoundary);

                if(mJoystick.getIsPressed()) {
                    mJoystick.draw(canvas);
                    mJoystick.update();
                }

                // Decoherence
                for (int i = decoArray.size() - 1; i >= 0; i--){
                    decoArray.get(i).drawDecoherence(canvas);
                }
                System.out.println("POS" + position + "; " + lastDecoPosition);
                if (position - lastDecoPosition > 50) {
                    if (Math.random() * 50 < 25){
                        System.out.println("HEYYYY");
                        generateDecoherence(canvas);
                        lastDecoPosition = position;
                    }
                }
                // End Decoherence


                canvas.restore();
                mSurfaceHolder.unlockCanvasAndPost(canvas);

                frameTime = (System.nanoTime() - frameStartTime) / 1000000;

                if (frameTime < (1000/FPS)) // if faster than the FPS -> wait until FPS matched
                {
                    try {
                        Thread.sleep((int)(1000/FPS) - frameTime);
                    } catch (InterruptedException e) {}
                }
            }
        }
    }

    private void generateDecoherence(Canvas canvas) {
        System.out.println("HEY");
        int y = 0;
        for (int i = 0; i < 5; i++){
            y += mViewHeight/7;
            if (Math.random() * 50 < 25) {
                DecoherenceSprite deco = new DecoherenceSprite(mViewWidth,y - 25,mViewWidth - 50,y + 25,-5, Color.RED, decoBitmap);
                decoArray.add(deco); // saved decoherence to array
                deco.drawDecoherence(canvas);
            }
        }
    }
}