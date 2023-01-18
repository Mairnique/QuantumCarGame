package com.chumaribelle.quantumcargame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
//    String TAG = "com.example.quantumcargame.sharedpreferences";
//    LifecycleData currentRun, lifeTime;
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
//    TextView tCurrent, tLifetime;
//    TextView greetingDisplay;
    // create ints for top 3 runs. display on home and end screen
    private GameView mGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // load shared preferences
//        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
//        // instantiate editor
//        editor = sharedPreferences.edit();
//        currentRun = new LifecycleData();
//        currentRun.duration = "Current Run";
//        // get lifecycle data from sharedpref as string
//        String lifecycleDataAsString = sharedPreferences.getString("lifetime", "");
//        //Instantiate new LifecycleData if empty string
//            //else convert JSON to LifecycleData object
//        if (lifecycleDataAsString.equals("")){
//            lifeTime = new LifecycleData();
//            lifeTime.duration = "Lifetime Data";
//        } else {
//            lifeTime = LifecycleData.parseJSON(lifecycleDataAsString);
//        }
//        //instantiate TextViews
//        tCurrent = findViewById(R.id.curernt);
//        tLifetime = findViewById(R.id.lifetime);
//        //display data on TextViews
//        tCurrent.setText(currentRun.toString());
//        tLifetime.setText(lifeTime.toString());
//
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        currentRun.updateEvent(currentEnclosingMethod);
//        lifeTime.updateEvent(currentEnclosingMethod);
//        displayData();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mGameView = new GameView(this);
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(mGameView);

    }
//
//    public void startGame(View view){
//        // game landscape
//        setContentView(R.layout.activity_main);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        mGameView = new GameView(this);
//        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        setContentView(mGameView);
//        System.out.println("STUFF!!!!!!!");
//    }
//
//    public void startInstructions(View view) {
//        setContentView(R.layout.instructions);
//    }
//
//    public void startScores(View view) {
//        setContentView(R.layout.scores);
//    }


//    //convert lifetime to String and store in SharedPreferences
//    public void storeData(){
//        editor.putString("lifetime",lifeTime.toJSON()).apply();
//    }
//
//    //display data on TextViews
//    public void displayData(){
//        tCurrent.setText(currentRun.toString());
//        tLifetime.setText(lifeTime.toString());
//    }

//    public void updateCount(String currentEnclosingMethod){
//        //pass name to LifecycleData to update count
//        lifeTime.updateEvent(currentEnclosingMethod);
//        currentRun.updateEvent(currentEnclosingMethod);
//        displayData();
//        storeData();
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }
//    @Override
//    protected void onPause(){
//        super.onPause();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }
//    @Override
//    protected void onStop(){
//        super.onStop();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }
//    @Override
//    protected void onRestart(){
//        super.onRestart();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        String currentEnclosingMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();
//        updateCount(currentEnclosingMethod);
//    }





}