package io.korigan.whosthatguy.model;

import java.text.SimpleDateFormat;

/**
 * Created by guillaume on 29/04/15.
 */
public class MDBUtils {

    public static final String TV = "tv";
    public static final String MOVIE = "movie";

    public static SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd/yyyy");


    public static String getTitle(String title, String name, String media_type){
        if(media_type.equals(TV)){
            return name;
        }
        else if(media_type.equals(MOVIE)){
            return title;
        }
        else return "";
    }
}
