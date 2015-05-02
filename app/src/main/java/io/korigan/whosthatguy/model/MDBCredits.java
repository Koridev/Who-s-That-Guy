package io.korigan.whosthatguy.model;

import org.parceler.Parcel;

/**
 * Created by guillaume on 23/04/15.
 */
@Parcel
public class MDBCredits {
    public String id;
    public MDBCast[] cast;

    public boolean hasActors(){
        return cast != null && cast.length > 0;
    }
}
