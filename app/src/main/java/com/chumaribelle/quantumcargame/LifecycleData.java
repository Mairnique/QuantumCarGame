package com.chumaribelle.quantumcargame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LifecycleData {
    int onCreate = 0;
    int onStart = 0;
    int onResume = 0;
    int onPause = 0;
    int onStop = 0;
    int onRestart = 0;
    int onDestroy = 0;
    int decrement = 0;
    int increment = 0;
    int top1 = 0;
    int top2 = 0;
    int top3 = 0;
    String date;
    String duration;

    public String toString(){
        return duration + "\n" +
                "onCreate: \t" + onCreate + "\n" +
                "onStart: \t" + onStart + "\n" +
                "onResume: \t" + onResume + "\n" +
                "onPause: \t" + onPause + "\n" +
                "onStop: \t" + onStop + "\n" +
                "onRestart: \t" + onRestart + "\n" +
                "decrement: \t" + decrement + "\n" +
                "increment: \t" + increment + "\n";
    }

    // convert instance to String
    String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, LifecycleData.class);
    }

    // from JSON String to class using GSON
    static LifecycleData parseJSON(String fromSharedPreferences) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(fromSharedPreferences, LifecycleData.class);
    }

    public void updateEvent(String currentEnclosingMethod) {
        switch (currentEnclosingMethod) {
            case "onCreate":
                onCreate++;
                break;
            case "onStart":
                onStart++;
                break;
            case "onResume":
                onResume++;
                break;
            case "onPause":
                onPause++;
                break;
            case "onStop":
                onStop++;
                break;
            case "onRestart":
                onRestart++;
                break;
            case "onDestroy":
                onDestroy++;
                break;
            case "increment":
                increment++;
                break;
            case "decrement":
                decrement++;
                break;
            default:break;
        }
    }

    public void reset() {
        onCreate = 0;
        onStart = 0;
        onResume = 0;
        onPause = 0;
        onStop = 0;
        onRestart = 0;
        onDestroy = 0;
        increment = 0;
        decrement = 0;
    }


}
