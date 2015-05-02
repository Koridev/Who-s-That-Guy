package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

/**
 * Created by guillaume on 23/04/15.
 */
@Parcel
public class MDBPerson extends MDBMedia{
    public String id;
    public String biography;
    public String birthday;
    public String homepage;
    public String name;
    public String profile_path;
}
