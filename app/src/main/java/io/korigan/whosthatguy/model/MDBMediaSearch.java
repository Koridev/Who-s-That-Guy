package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guillaume on 23/04/15.
 */
@Parcel
public class MDBMediaSearch {
    public int page;
    public MDBMedia[] results;
    public int total_pages;
    public int total_results;

    public List<MDBMedia> getMediaList(){
        return new LinkedList<>(Arrays.asList(results));
    }

    public int getMediaCount(){
        return total_results;
    }
}
