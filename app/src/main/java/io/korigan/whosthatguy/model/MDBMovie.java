package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guillaume on 23/04/15.
 */
@Parcel
public class MDBMovie extends MDBMedia{
    private String original_title;
    private String title;
    private String name;
    private String original_name;
    private float vote_average;
    private String poster_path;
    private Date release_date;
    private Date first_air_date;


    @Override
    public String toString(){
        return getTitle();
    }

    public String getTitle(){
        return MDBUtils.getTitle(original_title, original_name, media_type);
    }

    public String getMediaType(){
        return media_type;
    }

    public String getPosterPath(){
        return poster_path;
    }

    public String getReleaseDate(){

        if(media_type.equals(MDBUtils.MOVIE)){
            if(release_date != null) {
                return MDBUtils.displayFormat.format(release_date);
            }
        }
        else{
            if(first_air_date != null) {
                return MDBUtils.displayFormat.format(first_air_date);
            }
        }
        return "";
    }
}
