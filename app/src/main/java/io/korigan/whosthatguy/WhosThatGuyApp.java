package io.korigan.whosthatguy;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
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
}
