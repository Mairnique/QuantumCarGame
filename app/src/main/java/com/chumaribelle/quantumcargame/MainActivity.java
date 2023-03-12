package com.chumaribelle.quantumcargame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // create ints for top 3 runs. display on home and end screen
    private GameView mGameView;
    String TAG = "com.chumaribelle.quantumcargame";
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    TextView one, two, three, four, five;
    TextView[] views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mGameView = new GameView(this);

        // load shared preferences
        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        // instantiate editor
        editor = sharedPreferences.edit();

//        editor.putString("one", Integer.toString(Integer.MAX_VALUE)).commit();
//        editor.putString("two", Integer.toString(Integer.MAX_VALUE)).commit();
//        editor.putString("three", Integer.toString(Integer.MAX_VALUE)).commit();

//        views = new TextView[]{one, two, three, four, five};
//        layout = findViewById(R.id.scores);

    }

    protected void onPause() {
        super.onPause();
        mGameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
    }
    public void startScreen(View view){
        setContentView(R.layout.activity_main);
    }
    public void startGame(View view) {
        mGameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        mGameView.setStartTime(System.nanoTime());
        mGameView.setKeepScreenOn(true);
        setContentView(mGameView);
    }

    public void startInstructions(View view) {
        setContentView(R.layout.instructions);
    }

    public void startScores(View view) {

        setContentView(R.layout.scores);
        // set text views
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);

        one.setText("FIRST: "+ sharedPreferences.getString("one", "None"));
        two.setText("SECOND: "+ sharedPreferences.getString("two",  "None"));
        three.setText("THIRD: "+ sharedPreferences.getString("three", "None"));
    }

    public void resetScores(View view) {
        editor.clear().commit();
    }
}