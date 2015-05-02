package io.korigan.whosthatguy.network;

import io.korigan.whosthatguy.model.MDBActorCreditsList;
import io.korigan.whosthatguy.model.MDBCredits;
import io.korigan.whosthatguy.model.MDBMediaSearch;
import io.korigan.whosthatguy.model.MDBPerson;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by guillaume on 23/04/15.
 */
public interface MovieDBService {
    @GET("/search/multi")
    void mediaSearch(@Query("api_key") String apiKey, @Query("query") String title, Callback<MDBMediaSearch> cb);
    @GET("/{type}/{id}/credits")
    void movieCredits(@Query("api_key") String apiKey, @Path("type") String type, @Path("id") String id, Callback<MDBCredits> cb);
    @GET("/person/{id}")
    void getPerson(@Query("api_key") String apiKey, @Path("id") String id, Callback<MDBPerson> cb);
    @GET("/person/{id}/combined_credits")
    void getCredits(@Query("api_key") String apiKey, @Path("id") String id, Callback<MDBActorCreditsList> cb);
}
