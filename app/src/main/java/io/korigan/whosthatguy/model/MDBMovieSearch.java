package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guillaume on 23/04/15.
 */
@Parcel
public class MDBMovieSearch {
    public int page;
//    public List<MDBMovie> results;
    public MDBMovie[] results;
    public int total_pages;
    public int total_results;

    public List<String> getMoviesTitle(){
        List<String> result = new ArrayList<>();
        for(MDBMovie movie : Arrays.asList(results)){
            result.add(movie.getTitle());
        }
        return result;
    }
}
