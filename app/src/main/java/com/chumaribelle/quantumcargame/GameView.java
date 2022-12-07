package com.chumaribelle.quantumcargame;

import android.content.Context;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{

    Context mContext;

    public GameView(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    public void run() {

    }
}
