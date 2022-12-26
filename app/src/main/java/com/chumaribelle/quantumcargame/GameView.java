package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{

    private SurfaceHolder mSurfaceHolder;
    Context mContext;
    private int mViewWidth;
    private int mViewHeight;
    private CarSprite mCar;
    private boolean mRunning;
    private Thread mGameThread;
    private Paint mPaint;
    private Path mPath;

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

    @Override
    public void run() {
        Canvas canvas;
        while(mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                canvas = mSurfaceHolder.lockCanvas();
                canvas.save();

                drawBackground(canvas);

                mCar.draw(canvas);

                canvas.restore();
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
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
}
