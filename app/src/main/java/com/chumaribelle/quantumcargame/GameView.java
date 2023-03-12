package com.chumaribelle.quantumcargame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class GameView extends SurfaceView implements Runnable{

    public static final double MAX_UPS = 30.0;

    private SurfaceHolder mSurfaceHolder;
    Context mContext;
    // shared preferences
    String TAG = "com.chumaribelle.quantumcargame";
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

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
    private float position;
    // decoherence variables
    private DecoherenceSprite mDecoherence;
    private float lastDecoPosition;
    private Bitmap decoBitmap;
    private ArrayList<DecoherenceSprite> decoArray;
    private int speed;
    private int decoWidth;
    // probability variables
    private Bitmap probBitmap;
    private ArrayList<ProbabilitySprite> probArray;
    private int probWidth;
    // collisions
    private int probTotal;
    private int dProb;
    private float decoScreenStartTime;
    private float timeSinceDecoScreen;
    // finish line
    private Bitmap finBitmap;
    private FinishLineSprite finSprite;
    private int finLinePos;
    private Bitmap gameoverBitmap;
    private FinishLineSprite gameoverSprite;
    private Bitmap backBitmap;

    // time
    private int finalTime;
    long frameTime;
    private long startTime;

    private boolean inSuperposition;
    private long superpositionStart;
    private long timeSinceSuperStart;
    private boolean createSuperposition;

    private boolean createMeasurement;
    private long measurementStart;
    private long timeSinceMeasureStart;
    private boolean rewindBool;
    private Bitmap rewindBitmap;
    private Bitmap staticBitmap;
    private int posIncrement;

    private RectF emptyProgress;
    private RectF filledProgress;
    private float measurePos;

    int intervalNum = 0;
    private float rewindStart;
    private float timeSinceRewindStart;
    private int decoTime;
    private boolean decoScreenOn;
    private long clearStartTime;

    // back button
    private boolean end;
    private RectF back;
    private boolean reachedFinishLine;

    private Paint text;
    private Paint whiteText;

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
        speed = -1*(int) (mViewWidth/150);
        decoWidth = (int) (mViewWidth/18);
        probWidth = (int) (mViewWidth/20);
        probTotal = 50;
        dProb = 10;
        posIncrement = speed*-1;
        decoTime = 2;
        finLinePos = mViewWidth*50;
        frameTime = 0;
        end = false;


        // load shared preferences
        sharedPreferences = mContext.getSharedPreferences(TAG, MODE_PRIVATE);
        // instantiate editor
        editor = sharedPreferences.edit();


        // car bitmap
        carBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.futureracecar);
        carBitmap = Bitmap.createScaledBitmap(carBitmap, (int) (mViewWidth/9.5), (int) (mViewHeight/12.5), false);
        mCar = new CarSprite(20, (mViewHeight/2)-(carBitmap.getHeight()/2), 20+carBitmap.getWidth(),
                (mViewHeight/2)+(carBitmap.getHeight()/2), 0, 0, carBitmap, (double) mViewWidth/5);
        mJoystick = new Joystick(200, mViewHeight-200, mViewWidth/40, mViewWidth/120);
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
        finSprite = new FinishLineSprite(mViewWidth - mViewWidth / 8, 0, mViewWidth, mViewHeight, speed, Color.RED, finBitmap, backBitmap);
        inSuperposition = false;

        // Rewind Image
        rewindBitmap =  BitmapFactory.decodeResource(mContext.getResources(), R.drawable.rewind);
        rewindBitmap = Bitmap.createScaledBitmap(rewindBitmap, mViewWidth/8, mViewHeight/8, false);

        // Static Image
        staticBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.staticimage);
        staticBitmap = Bitmap.createScaledBitmap(staticBitmap, mViewWidth, mViewHeight, false);

        // Progress Bar
        emptyProgress = new RectF((float) mViewWidth/32, (float) mViewHeight/32, (float) mViewWidth*10/16, (float) mViewHeight/8);
        filledProgress = new RectF(emptyProgress.left+5, emptyProgress.top+5, emptyProgress.left+5, emptyProgress.bottom-5);

        // Game Over Setup
        gameoverBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gameover);
        gameoverBitmap = Bitmap.createScaledBitmap(gameoverBitmap, mViewWidth/2, mViewHeight / 2, false);

        // Back Button
        back = new RectF(0, 0, 200, 200);
        backBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back);
        backBitmap = Bitmap.createScaledBitmap(backBitmap, (int)back.width(), (int)back.height(), false);

        text = new Paint();
        text.setTextSize((float) (mViewWidth/40));
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.goldman_bold);
        text.setTypeface(typeface);
        text.setTextAlign(Paint.Align.LEFT);

        whiteText = new Paint();
        whiteText.setTextSize((float) (mViewWidth/40));
        whiteText.setTypeface(typeface);
        whiteText.setTextAlign(Paint.Align.LEFT);
        whiteText.setColor(Color.WHITE);

    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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
                mJoystick = new Joystick((int) event.getX(), (int) event.getY(), mViewWidth/20, mViewWidth/50);
                if(mJoystick.isPressed(event.getX(), event.getY())) {
                    mJoystick.setIsPressed(true);
                }
                if (end == true && back.contains((int) event.getX(), (int) event.getY())){
                    Intent i = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(i);
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
        mPaint.setColor(Color.rgb(113, 245, 252));
        canvas.drawRect(0,0,mViewWidth,(float) mViewHeight/7, mPaint);
        canvas.drawRect(0, (float) mViewHeight*6/7, mViewWidth, mViewHeight, mPaint);

        // Road
        mPaint.setColor(Color.rgb(70, 70, 70));
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
            if (inSuperposition) {
                if (Math.random() * 50 < 4) {
                    DecoherenceSprite deco = new DecoherenceSprite(mViewWidth, y, mViewWidth + decoWidth, y + decoWidth, speed, Color.RED, decoBitmap);
                    decoArray.add(deco); // saved decoherence to array
                    deco.drawDecoherence(canvas);
                }
                else if (Math.random() * 50 < 2.5) {
                    ProbabilitySprite prob = new ProbabilitySprite(mViewWidth,y ,mViewWidth + probWidth,y + probWidth,speed, Color.RED, probBitmap);
                    probArray.add(prob); // saved decoherence to array
                    prob.drawProbability(canvas);
                }
            }
            else if (Math.random() * 50 < 4) {
                DecoherenceSprite deco = new DecoherenceSprite(mViewWidth, y, mViewWidth + decoWidth, y + decoWidth, speed, Color.RED, decoBitmap);
                decoArray.add(deco); // saved decoherence to array
                deco.drawDecoherence(canvas);
            }
        }
    }

    private void clearScreen(Canvas canvas, float clearScreenStartTime) {
        Paint p = new Paint();
        p.setColor(Color.rgb(113, 245, 252));
        float timeSinceClearStart = (System.nanoTime() - clearScreenStartTime)/1000000000;
        if (timeSinceClearStart < 0.25)
            p.setAlpha((int) (timeSinceClearStart*250));
        if (timeSinceClearStart > 0.25)
            p.setAlpha((int) ((0.5-timeSinceClearStart)*250));
        canvas.drawRect(0, 0, mViewWidth, mViewHeight, p);
    }

    private void decoScreen(Canvas canvas, float decoScreenStartTime){
        Paint p = new Paint();
        float timeSinceDecoStart = (System.nanoTime() - decoScreenStartTime)/1000000000;
        if (timeSinceDecoStart < 1)
            p.setAlpha((int) (timeSinceDecoStart*255));
        if (timeSinceDecoStart > decoTime-1)
            p.setAlpha((int) ((decoTime-timeSinceDecoStart)*255));
        canvas.drawBitmap(staticBitmap,0,0, p);
    }

    private void superposition(Canvas canvas, Paint text) {
        timeSinceSuperStart = System.nanoTime() - superpositionStart;
        int secondsVal = (3 - (int) (timeSinceSuperStart/1000000000));

        String superString = "";
        superString = "Superposition in " + secondsVal;

        canvas.drawText(superString, (float) mViewWidth *11/16, (float) mViewHeight/12, text);
        if (secondsVal <= 0) {
            createSuperposition = false;
            inSuperposition = true;
            superCar = new SuperpositionCar(mCar, speed, carBitmap, position);
        }
    }

    private void rewind(Canvas canvas, Paint alphaPaint) {
        mJoystick.setIsPressed(false);
        posIncrement = 0;
        timeSinceRewindStart = System.nanoTime() - rewindStart;
        int secondsVal = (3 - (int) (timeSinceRewindStart/1000000000));
        int tempIntervalNum = (int) timeSinceRewindStart / 1000000;
        float superCarPos = superCar.pos;
        if (position > superCarPos && tempIntervalNum > intervalNum) {
            position = position - ((tempIntervalNum-intervalNum)*((measurePos - superCarPos) / 3) / 1000);
            intervalNum = tempIntervalNum;
        }
        // Static
        canvas.drawBitmap(staticBitmap, 0, 0, alphaPaint);
        if (secondsVal%2 != 0)
            canvas.drawBitmap(rewindBitmap, (float) mViewWidth/2-(float)rewindBitmap.getWidth()/2, (float) mViewHeight/2-(float)rewindBitmap.getHeight()/2, new Paint());
        if (secondsVal <= 0) {
            posIncrement = speed*-1;
            probTotal = 50;
            rewindBool = false;
            mCar = new CarSprite(20, (int) superCar.top, 20+carBitmap.getWidth(), (int) superCar.bottom,0 ,0, carBitmap, (double) mViewWidth/5);
            position = superCarPos;
            lastDecoPosition = position;
        }
    }

    private void measure(Canvas canvas, Paint text) {
        timeSinceMeasureStart = System.nanoTime() - measurementStart;
        int secondsVal = (3 - (int) (timeSinceMeasureStart/1000000000));

        String measureString = "";
        measureString = "Measurement in " + secondsVal;


        canvas.drawText(measureString, (float) mViewWidth *11/16, (float) mViewHeight/12, text);

        if (secondsVal <= 0) {
            createMeasurement = false;
            inSuperposition = false;
            double measurement = Math.random() * 100;
            if (measurement < probTotal) {
                if (decoArray.size() > 0) {
                    decoArray.subList(0, decoArray.size()).clear();
                }
                clearStartTime = System.nanoTime();
            }
            else {
                measurePos = position;
                rewindBool = true;
                rewindStart = System.nanoTime();
            }
            probTotal = 50;
            probArray.subList(0, probArray.size()).clear();
        }
    }


    @Override
    public void run() {
        // Variables
        Canvas canvas;
        long frameStartTime;
        final int FPS = 60;

        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(60);

        Paint empty = new Paint();
        empty.setStyle(Paint.Style.STROKE);
        empty.setStrokeWidth(5);

        Paint filled = new Paint();

        // Running stuff
        while(mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                this.position += posIncrement; // update position
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


                // Decoherence
                for (int i = decoArray.size() - 1; i >= 0; i--){
                    decoArray.get(i).drawDecoherence(canvas);
                    if (!decoArray.get(i).updateOk(canvas)){
                        decoArray.remove(i);
                    }
                    // if car intersects decoherence, delete decoherence and make screen white with static
                    else if ((decoArray.get(i)).intersect(mCar)){
//                        decoScreen(canvas, position); // draw the deco screen
                        decoArray.remove(i);
                        posIncrement = 2;
                        if (inSuperposition) {
                            if (probTotal > 40)
                                probTotal = ((int) (Math.random()*5))*10;
                            else
                                probTotal = ((int) (Math.random()*(probTotal/10)))*10;
                        }
                        if (decoScreenOn)
                            decoTime += 0.5;
                        else {
                            decoScreenOn = true;
                            decoScreenStartTime = System.nanoTime();
                        }
                    }
                }

                // Probability tokens
                for (int i = probArray.size() - 1; i >= 0; i--){
                    probArray.get(i).drawProbability(canvas);
                    if (!probArray.get(i).updateOk(canvas)){
                        probArray.remove(i);
                    }
                    // probability collide with car, delete it and add to probTotal
                    else if ((probArray.get(i)).intersect(mCar)){
                        probArray.remove(i);
                        if (probTotal < 100)
                            probTotal += dProb;
                    }
                }

                // Clear Screen
                if ((System.nanoTime() - clearStartTime)/(float)1000000000 < 0.5) {
                    clearScreen(canvas, clearStartTime);
                }


                // Deco Screen
                if ((System.nanoTime() - decoScreenStartTime)/1000000000 < decoTime && !rewindBool) {
                    decoScreen(canvas, decoScreenStartTime);
                }
                if ((System.nanoTime() - decoScreenStartTime)/1000000000 > decoTime && !rewindBool) {
                    posIncrement = -1*speed;
                    decoTime = 2;
                    decoScreenOn = false;
                }

                // each 10 frames
                if (position - lastDecoPosition > decoWidth) {
                    if (Math.random() * 50 < 4 && !(position > finLinePos) && !rewindBool && !reachedFinishLine){
                        generateDecoherenceProbability(canvas);
                        lastDecoPosition = position;
                    }
                }

                if (reachedFinishLine) {
                    if (decoArray.size() > 0) {
                        decoArray.subList(0, decoArray.size()).clear();
                    }
                    probArray.subList(0, probArray.size()).clear();

                }


                // Updating superposition

                if (position - lastDecoPosition > decoWidth && !createSuperposition && !inSuperposition) {
                    double superRand = (Math.random() * 250);
                    if (superRand < 1) {
                        createSuperposition = true; // Determine whether to create superposition
                        superpositionStart = System.nanoTime();
                    }
                }
                if (createSuperposition && !inSuperposition) {
                    superposition(canvas, text);
                }

                // Measurement

                if (position - lastDecoPosition > decoWidth && !createMeasurement && inSuperposition) {
                    double measureRand = (Math.random() * 400);
                    if (measureRand < 1) {
                        createMeasurement = true; // Determine whether to create superposition
                        measurementStart = System.nanoTime();
                    }
                }

                if (createMeasurement && inSuperposition) {
                    measure(canvas, text);
                }

                // Rewind
                if(rewindBool) {
                    rewind(canvas, alphaPaint);
                }



                // Finish Line Animations
                if (position > finLinePos - (mViewWidth - finSprite.width())) {

                    // fin line animations
                    System.out.println("Reached FINLINE POS _______________________________________________________________");
                    if (finSprite.updateOk(canvas) != false) {
                        finSprite.drawFinishLine(canvas);
                        reachedFinishLine = true;
                    }
                    System.out.println("END FINLINE POS _______________________________________________________________");

                }

                // update shared preferences
                if (position > finLinePos) {
                    System.out.println("start shared _______________________________________________________________");

                    // variables
                    int first = Integer.parseInt(sharedPreferences.getString("one", Integer.toString(Integer.MAX_VALUE)));
                    int second = Integer.parseInt(sharedPreferences.getString("two", Integer.toString(Integer.MAX_VALUE)));
                    int third = Integer.parseInt(sharedPreferences.getString("three", Integer.toString(Integer.MAX_VALUE)));
                    ArrayList<Integer> topScores = new ArrayList<Integer>(Arrays.asList(first, second, third));

                    finalTime = (int) ((System.nanoTime() - startTime)/1000000000);
                    System.out.println("FINAL TIME: " + finalTime);
                    System.out.println("CURRENT TIME: " + System.nanoTime());
                    System.out.println("FRAME TIME: " + frameTime);
                    System.out.println("START TIME: " + startTime);


                    for (int i = 0; i < topScores.size(); i++){
                        if (finalTime < topScores.get(i)) {
                            topScores.add(i, finalTime);
                            break;
                        }
                    }
                    editor.putString("one", Integer.toString(topScores.get(0))).commit();
                    editor.putString("two", Integer.toString(topScores.get(1))).commit();
                    editor.putString("three", Integer.toString(topScores.get(2))).commit();
                    finSprite.drawFinishLine(canvas);
                    finSprite.drawFinText(canvas,mViewWidth,mViewHeight,gameoverBitmap,Integer.toString(finalTime), text);


                    end = true;
                    canvas.drawBitmap(backBitmap, null, back, new Paint());

                    mRunning = false;
                    System.out.println("end shared _______________________________________________________________");

//                    if (position > finLinePos + mViewWidth) {
//                    finSprite.drawFinishLineScreen(canvas, mViewWidth, mViewHeight);
//                    }
                }





                // Draw Progress
                float progress = (float) position / finLinePos * (emptyProgress.width()-10);
                filledProgress.right = filledProgress.left + progress;
                if (!end) {
                    canvas.drawRect(emptyProgress, empty);
                    canvas.drawRect(filledProgress, filled);
                }

                if (inSuperposition && !end) {
                    canvas.drawText("Probability of clear: " + probTotal, (float) mViewWidth*1/32, (float) mViewHeight*15/16, text);
                    canvas.drawText("Probability of going back: " + (100-probTotal), (float) mViewWidth*8/16, (float) mViewHeight*15/16, text);
                }

                int timeSinceStart = (int) ((System.nanoTime() - startTime)/1000000000);
                int seconds = timeSinceStart%60;
                if (seconds < 10) {
                    canvas.drawText("" + timeSinceStart/60 + ":0" + timeSinceStart%60, (float) mViewWidth*1/22, (float) mViewHeight*1/10, whiteText);
                }
                else
                    canvas.drawText("" + timeSinceStart/60 + ":" + timeSinceStart%60, (float) mViewWidth*1/22, (float) mViewHeight*1/10, whiteText);


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

