package io.korigan.whosthatguy.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by guillaume on 02/05/15.
 */
public class MDBMediaDeserializer implements JsonDeserializer<MDBMedia> {

    private static Map<String, Class> map = new TreeMap<String, Class>();

    static{
        map.put(MDBUtils.MOVIE, MDBMovie.class);
        map.put(MDBUtils.TV, MDBMovie.class);
        map.put(MDBUtils.PERSON, MDBPerson.class);
    }

    @Override
    public MDBMedia deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String media_type = json.getAsJsonObject().get("media_type").getAsString();

        return context.deserialize(json, map.get(media_type));
    }
}
