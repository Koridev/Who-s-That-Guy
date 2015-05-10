package io.korigan.whosthatguy;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import io.korigan.whosthatguy.model.MDBMedia;
import io.korigan.whosthatguy.model.MDBMediaDeserializer;
import io.korigan.whosthatguy.model.MDBPerson;

/**
 * Created by guillaume on 29/04/15.
 */
public class WhosThatGuyApp extends Application {

    private static WhosThatGuyApp mThis;

    private Gson mGson;

    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mThis = this;

        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>(){
                    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        if(json.getAsString().isEmpty()) return null;
                        try {
                            return df.parse(json.getAsString());
                        } catch (final Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .registerTypeAdapter(MDBMedia.class, new MDBMediaDeserializer())
                .create();
    }

    public static WhosThatGuyApp get(){
        return mThis;
    }

    public Gson getGson(){
        return mGson;
    }

    public enum TrackerName {
        APP_TRACKER(0);

        TrackerName(int value){
            this.mValue = value;
        }

        private int mValue;

        public int getValue(){
            return mValue;
        }
    };

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized public Tracker getTracker(TrackerName trackerName){
        if(!mTrackers.containsKey(trackerName)){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerName, t);
        }
        return mTrackers.get(trackerName);
    }

    public void sendScreenView(String screenName){
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
    }


    public void sendTrackingEvent(String category, String action, String label, long value){
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    public void sendTrackingEvent(String category, String action, String label){
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
