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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable{

    public static final double MAX_UPS = 30.0;

    private SurfaceHolder mSurfaceHolder;
    Context mContext;
    private int mViewWidth;
    private int mViewHeight;
    private CarSprite mCar;
    private Bitmap carBitmap;
    private SuperpositionCar superCar;
    private boolean mRunning;
    private Thread mGameThread;
    private Paint mPaint;
    private Path mPath;
    private Joystick mJoystick;
    private RectF mBoundary;
    private int position;
    // decoherence variables
    private DecoherenceSprite mDecoherence;
    private int lastDecoPosition;
    private Bitmap decoBitmap;
    private ArrayList<DecoherenceSprite> decoArray;
    private int speed;
    private int decoWidth;
    // probability variables
    private Bitmap probBitmap;
    private ArrayList<ProbabilitySprite> probArray;
    private int lastProbPosition;
    private int probWidth;
    // collisions
    private int probTotal;
    private int dProb;
    private int decoScreenPosition;
    // finish line
    private Bitmap finBitmap;
    private FinishLineSprite finSprite;
    private int finLinePos;

    boolean inSuperposition;
    long superpositionStart;
    long timeSinceSuperStart;
    private boolean createMeasurement;
    private long measurementStart;
    private long timeSinceMeasureStart;
    private long slowDownStart;
    private long timeSinceSlowDownStart;
    private boolean rewind;
    private Bitmap rewindBitmap;
    private Bitmap staticBitmap;


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
        speed = -10;
        decoWidth = (int) (mViewWidth/18);
        probWidth = (int) (mViewWidth/20);
        probTotal = 50;
        dProb = 10;
        decoScreenPosition = 0;
        finLinePos = 10000;

        // car bitmap
        carBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.racecar);
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
        decoBitmap = Bitmap.createScaledBitmap(decoBitmap, decoWidth, decoWidth, false);
        decoArray = new ArrayList<>();

        // Probability Setup
        probBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.probability);
        probBitmap = Bitmap.createScaledBitmap(probBitmap, probWidth, probWidth, false);
        probArray = new ArrayList<>();

        // Finish Line Setup
        finBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.finishline);
        finBitmap = Bitmap.createScaledBitmap(finBitmap, mViewWidth / 8, mViewHeight, false);
        finSprite = new FinishLineSprite(mViewWidth - mViewWidth / 8, 0, mViewWidth, mViewHeight, speed, Color.RED, finBitmap);

        inSuperposition = false;

        // Rewind Image
        rewindBitmap =  BitmapFactory.decodeResource(mContext.getResources(), R.drawable.rewind);
        rewindBitmap = Bitmap.createScaledBitmap(rewindBitmap, mViewWidth/8, mViewHeight/8, false);

        // Static Image
        staticBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.staticimage);
        staticBitmap = Bitmap.createScaledBitmap(staticBitmap, mViewWidth, mViewHeight, false);

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

    private void generateDecoherenceProbability(Canvas canvas) {
        int y = 0;
        for (int i = 0; i < 5; i++){
            y += mViewHeight/7;
            if (Math.random() * 50 < 4) {
                DecoherenceSprite deco = new DecoherenceSprite(mViewWidth,y,mViewWidth - 50,y + decoWidth/2,speed, Color.RED, decoBitmap);
                decoArray.add(deco); // saved decoherence to array
                deco.drawDecoherence(canvas);
            }
            else if (Math.random() * 50 < 4) {
                ProbabilitySprite prob = new ProbabilitySprite(mViewWidth,y ,mViewWidth - 50,y + probWidth/2,speed, Color.RED, probBitmap);
                probArray.add(prob); // saved decoherence to array
                prob.drawProbability(canvas);
            }
        }
    }

    private void decoScreen(Canvas canvas){
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawRect(0,0,mViewWidth,mViewHeight, p);
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
        boolean createSuperposition = false;
        Paint text = new Paint();
        text.setTextSize(55);
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.goldman_bold);
        text.setTypeface(typeface);
        text.setTextAlign(Paint.Align.LEFT);


        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(60);

        // Running stuff
        while(mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                this.position += speed * -1; // update position
//                System.out.println("Current Position: " + position);
                // record start time for run
                frameStartTime = System.nanoTime();

                canvas = mSurfaceHolder.lockCanvas();
                canvas.save();

                drawBackground(canvas);

                mCar.draw(canvas);
                mCar.update(mJoystick, mBoundary);

                if(inSuperposition && superCar.inBounds) {
                    superCar.draw(canvas);
                    superCar.update(mBoundary);
                }

                if(mJoystick.getIsPressed()) {
                    mJoystick.draw(canvas);
                    mJoystick.update();
                }

                if (inSuperposition) {
                    canvas.drawText("Probability of time-slow: " + probTotal, (float) mViewWidth*1/16, 100, text);
                    canvas.drawText("Probability of reset to split: " + (100-probTotal), (float) mViewWidth*1/16, mViewHeight-50, text);
                }

                // Decoherence
                for (int i = decoArray.size() - 1; i >= 0; i--){
                    decoArray.get(i).drawDecoherence(canvas);
                    if (decoArray.get(i).updateOk(canvas) == false){
                        decoArray.remove(i);
                    }
                    // if car intersects decoherence, delete decoherence and make screen white with static
                    else if ((decoArray.get(i)).intersect(mCar)){
                        decoScreen(canvas); // draw the deco screen
                        decoScreenPosition = position;
                        decoArray.remove(i);
                        probTotal = 50;
                    }
                }

                // each 10 frames
                if (position - lastDecoPosition > mViewWidth/10) {
                    if (Math.random() * 50 < 4 && !(position > finLinePos)){
                        generateDecoherenceProbability(canvas);
                        lastDecoPosition = position;
                    }
                }

                if (position - decoScreenPosition < 500) {
                    decoScreen(canvas);
                }

                // Updating superposition

                if (position - lastDecoPosition > decoWidth && !createSuperposition && !inSuperposition) {
                    double superRand = (Math.random() * 500);
                    if (superRand < 1) {
                        createSuperposition = true; // Determine whether to create superposition
                        superpositionStart = System.nanoTime();
                    }
                }
                if (createSuperposition && !inSuperposition) {

                    timeSinceSuperStart = System.nanoTime() - superpositionStart;
                    int secondsVal = (3 - (int) (timeSinceSuperStart/1000000000));
                    System.out.println(timeSinceSuperStart/1000000000);

                    String superString = "";
                    superString = "Superposition in " + secondsVal;

                    canvas.drawText(superString, (float) mViewWidth *11/16, 100, text);
                    if (secondsVal <= 0) {
                        createSuperposition = false;
                        inSuperposition = true;
                        superCar = new SuperpositionCar(mCar, speed, carBitmap, position);
                    }
                }

                // Measurement

                if (position - lastDecoPosition > decoWidth && !createMeasurement && inSuperposition) {
                    double measureRand = (Math.random() * 500);
                    if (measureRand < 1) {
                        createMeasurement = true; // Determine whether to create superposition
                        measurementStart = System.nanoTime();
                    }
                }

                if (createMeasurement && inSuperposition) {
                    timeSinceMeasureStart = System.nanoTime() - measurementStart;
                    int secondsVal = (3 - (int) (timeSinceMeasureStart/1000000000));
                    System.out.println(timeSinceMeasureStart/1000000000);

                    String measureString = "";
                    measureString = "Measurement in " + secondsVal;


                    canvas.drawText(measureString, (float) mViewWidth *11/16, 100, text);

                    if (secondsVal <= 0) {
                        createMeasurement = false;
                        inSuperposition = false;
                        double measurement = Math.random() * 100;
                        probTotal = 50;
                        if (measurement < probTotal) {
                            slowDownStart = System.nanoTime();
                            speed = -2;
                        }
                        else {
                            rewind = true;
                            position = superCar.pos;
                        }
                    }
                }

                // Rewind
                if(rewind) {
                    mJoystick.setIsPressed(false);
                    timeSinceMeasureStart = System.nanoTime() - measurementStart;
                    int secondsVal = (8 - (int) (timeSinceMeasureStart/1000000000));
                    // Static
                    canvas.drawBitmap(staticBitmap, 0, 0, alphaPaint);
                    if (secondsVal%2 != 0)
                        canvas.drawBitmap(rewindBitmap, (float) mViewWidth/2-(float)rewindBitmap.getWidth()/2, (float) mViewHeight/2-(float)rewindBitmap.getHeight()/2, new Paint());
                    if (secondsVal <= 0) {
                        rewind = false;
                        mCar = new CarSprite(20, (int) superCar.top, 20+carBitmap.getWidth(), (int) superCar.bottom,0 ,0, carBitmap);
                    }
                }

                // Slow down
                timeSinceSlowDownStart = System.nanoTime() - slowDownStart;
                if (timeSinceSlowDownStart >= 5L *1000000000) {
                    speed = -10;
                }

                // Probability
                for (int i = probArray.size() - 1; i >= 0; i--){
                    if (position - decoScreenPosition >= 500) {
                        probArray.get(i).drawProbability(canvas);
                    }
                    if (probArray.get(i).updateOk(canvas) == false){
                        probArray.remove(i);
                    }
                    // probability collide with car, delete it and add to probTotal
                    else if ((probArray.get(i)).intersect(mCar)){
                        probArray.remove(i);
                        probTotal += dProb;
                        System.out.println(probTotal);
                    }
                }

                if (position > finLinePos) {
                    finSprite.drawFinishLine(canvas);
                    finSprite.updateOk(canvas);

//                    if (position > finLinePos + mViewWidth) {
                        finSprite.drawFinishLineScreen(canvas, mViewWidth, mViewHeight);
//                    }
                }

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


}