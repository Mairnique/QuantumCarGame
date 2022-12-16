package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        Bitmap carBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.racecar);
        carBitmap = Bitmap.createScaledBitmap(carBitmap, (int) (mViewWidth/6.5), mViewHeight/8, false);
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
                canvas.drawColor(Color.WHITE);
                mCar.draw(canvas);

                canvas.restore();
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
