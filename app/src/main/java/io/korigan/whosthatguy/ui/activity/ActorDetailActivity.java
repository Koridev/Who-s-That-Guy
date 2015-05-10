package io.korigan.whosthatguy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.WhosThatGuyApp;
import io.korigan.whosthatguy.model.MDBActorCreditsList;
import io.korigan.whosthatguy.network.GenericCallback;
import io.korigan.whosthatguy.network.MovieDBService;
import io.korigan.whosthatguy.model.MDBPerson;
import io.korigan.whosthatguy.ui.adapter.AppearanceAdapter;
import io.korigan.whosthatguy.ui.decorator.DividerItemDecoration;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ActorDetailActivity extends AppCompatActivity {

    public static final String ACTOR_ID = "ACTOR_ID";
    private static final String TAG = "ActorDetailActivity";

    private MovieDBService mTMDBService;

    private ProgressBar mProgressBar;
//    private TextView mTVAppearances;

    private RecyclerView mAppearancesList;
    private AppearanceAdapter mAppearanceAdapter;

    private MDBPerson mPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mAppearancesList = (RecyclerView) findViewById(R.id.list_appearances);

        mAppearancesList.setLayoutManager(new LinearLayoutManager(this));
        mAppearanceAdapter = new AppearanceAdapter(this, (ViewGroup)findViewById(R.id.list_parent));
        mAppearancesList.addItemDecoration(new DividerItemDecoration(this,
                getResources().getColor(R.color.background)));
        mAppearancesList.setAdapter(mAppearanceAdapter);

        String actorId = getIntent().getStringExtra(ACTOR_ID);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint_tmdb))
                .setConverter(new GsonConverter(WhosThatGuyApp.get().getGson()))
                .build();

        mTMDBService = restAdapter.create(MovieDBService.class);

        getActorData(actorId);
    }

    @Override
    public void onNewIntent(Intent i){
        super.onNewIntent(i);
        setIntent(i);
        String actorId = i.getStringExtra(ACTOR_ID);
        if(mPerson != null) {
            resetView();
            mProgressBar.setVisibility(View.VISIBLE);
            if (!actorId.equals(mPerson.id)) {
                getActorData(actorId);
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        WhosThatGuyApp.get().sendScreenView("view.actor_detail");
    }

    private void getActorData(final String actorId){
        mTMDBService.getPerson(getString(R.string.apikey), actorId,
                new GenericCallback<MDBPerson>(ActorDetailActivity.this) {

                    @Override
                    public void success(MDBPerson mdbPerson, Response response) {
                        mPerson = mdbPerson;
                        getSupportActionBar().setTitle(mdbPerson.name);
                        mAppearanceAdapter.setPersonInfo(mdbPerson);
                        mAppearancesList.getLayoutManager().scrollToPosition(0);

                        if(mdbPerson.biography != null && !mdbPerson.biography.isEmpty()) {
                            mProgressBar.setVisibility(View.INVISIBLE);

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        WhosThatGuyApp.get().sendTrackingEvent(
                                getString(R.string.category_error),
                                getString(R.string.error_network),
                                error.getMessage() + " (while fetching person)");
                        super.failure(error);
                    }
                });

        mTMDBService.getCredits(getString(R.string.apikey), actorId,
                new GenericCallback<MDBActorCreditsList>(ActorDetailActivity.this){

                    @Override
                    public void success(MDBActorCreditsList mdbActorCreditsList, Response response) {
                        if(mdbActorCreditsList.getAppearanceCount() > 0){

                            mAppearanceAdapter.setAppearanceList(mdbActorCreditsList.getAppearances());
                            mAppearanceAdapter.notifyDataSetChanged();
                        }

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        WhosThatGuyApp.get().sendTrackingEvent(
                                getString(R.string.category_error),
                                getString(R.string.error_network),
                                error.getMessage()+" (while fetching credits)");
                        super.failure(error);
                    }
                });
    }

    private void resetView(){
        getSupportActionBar().setTitle("");
        mAppearanceAdapter.clear();
        mAppearanceAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actor_detail, menu);
        return true;
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
