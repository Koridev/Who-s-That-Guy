package io.korigan.whosthatguy;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by guillaume on 29/04/15.
 */
public class WhosThatGuyApp extends Application {

    private static WhosThatGuyApp mThis;

    private Gson mGson;

    @Override
    public void onCreate(){
        super.onCreate();
        mThis = this;

        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    public static WhosThatGuyApp get(){
        return mThis;
    }

    public Gson getGson(){
        return mGson;
    }
}
