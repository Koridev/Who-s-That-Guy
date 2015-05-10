package io.korigan.whosthatguy.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Arrays;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.WhosThatGuyApp;
import io.korigan.whosthatguy.model.MDBActorCreditsList;
import io.korigan.whosthatguy.model.MDBCredits;
import io.korigan.whosthatguy.network.MovieDBService;
import io.korigan.whosthatguy.ui.adapter.ActorAdapter;
import io.korigan.whosthatguy.ui.decorator.DividerItemDecoration;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MovieDetailActivity extends ActionBarActivity {

//    public static final String MOVIE_ID = "MOVIE_ID";
    public static final String MEDIA_TYPE = "MEDIA_TYPE";
    public static final String MOVIE_CREDIT = "MOVIE_CREDIT";

    private MovieDBService mTMDBService;

    private ProgressBar mProgressBar;
    private RecyclerView mActorListView;
    private View mNoActorView;

    private ActorAdapter mActorAdapter;

    private MDBCredits mCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mActorListView = (RecyclerView) findViewById(R.id.list_actors);
        mNoActorView = findViewById(R.id.view_no_actor);

        mActorListView.setLayoutManager(new LinearLayoutManager(this));
        mActorAdapter = new ActorAdapter(this);
        mActorListView.addItemDecoration(new DividerItemDecoration(this,
                getResources().getColor(R.color.background)));
        mActorListView.setAdapter(mActorAdapter);

        String mediaType = getIntent().getStringExtra(MEDIA_TYPE);

        MDBActorCreditsList.MDBActorCredit movieCredits = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_CREDIT));
        String movieID = movieCredits.id;
        String title = movieCredits.getTitle();

        getSupportActionBar().setTitle(title);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint_tmdb))
                .setConverter(new GsonConverter(WhosThatGuyApp.get().getGson()))
                .build();

        mTMDBService = restAdapter.create(MovieDBService.class);

        mNoActorView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTMDBService.movieCredits(getString(R.string.apikey),
                mediaType, movieID,
                new Callback<MDBCredits>(){

                    @Override
                    public void success(MDBCredits mdbCredits, Response response) {
                        setCast(mdbCredits);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MovieDetailActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        WhosThatGuyApp.get().sendTrackingEvent(
                                getString(R.string.category_error),
                                getString(R.string.error_network),
                                error.getMessage()+" (while fetching movieCredits)");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mNoActorView.setVisibility(View.VISIBLE); //TODO not this view
                    }
                });

    }

    private void setCast(MDBCredits credits){
        mCredits = credits;
        mActorAdapter.setActorList(Arrays.asList(credits.cast));
        mActorAdapter.notifyDataSetChanged();
        mNoActorView.setVisibility(View.INVISIBLE);

        if(credits.hasActors()) {
            mNoActorView.setVisibility(View.INVISIBLE);
        }
        else{
            mNoActorView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        WhosThatGuyApp.get().sendScreenView("view.movie_detail");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.transition.hold, R.transition.slide_right_out);
    }
}
