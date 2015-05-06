package io.korigan.whosthatguy.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.WhosThatGuyApp;
import io.korigan.whosthatguy.model.MDBActorCreditsList;
import io.korigan.whosthatguy.network.MovieDBService;
import io.korigan.whosthatguy.model.MDBPerson;
import io.korigan.whosthatguy.ui.transformation.CircleTransformation;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ActorDetailActivity extends ActionBarActivity {

    public static final String ACTOR_ID = "ACTOR_ID";
    private static final String TAG = "ActorDetailActivity";

    private MovieDBService mTMDBService;

    private ProgressBar mProgressBar;
    private TextView mTVAppearances;
    private TextView mTVBiography;
    private ImageView mImgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mTVAppearances = (TextView) findViewById(R.id.tv_appearances);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);
        mTVBiography = (TextView) findViewById(R.id.tv_biography);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);


        String actorId = getIntent().getStringExtra(ACTOR_ID);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint_tmdb))
                .setConverter(new GsonConverter(WhosThatGuyApp.get().getGson()))
                .build();

        mTMDBService = restAdapter.create(MovieDBService.class);

        mTMDBService.getPerson(getString(R.string.apikey), actorId,
                new Callback<MDBPerson>() {

                    @Override
                    public void success(MDBPerson mdbPerson, Response response) {
                        getSupportActionBar().setTitle(mdbPerson.name);
                        if(mdbPerson.biography != null && !mdbPerson.biography.isEmpty()){
                            (findViewById(R.id.tv_label_biography))
                                    .setVisibility(View.VISIBLE);
                            mTVBiography.setText(mdbPerson.biography);
                            mTVBiography.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                        if(mdbPerson.profile_path != null && !mdbPerson.profile_path.isEmpty()) {
                            Picasso.with(ActorDetailActivity.this)
                                    .load(getString(R.string.endpoint_tmdb_img)+"/w300"+mdbPerson.profile_path)
                                    .centerCrop()
                                    .fit()
                                    .error(R.drawable.ic_person_big)
                                    .transform(new CircleTransformation())
                                    .into(mImgProfile);
                        }
                        else{
                            mImgProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_big));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ActorDetailActivity.this, "Oups...", Toast.LENGTH_SHORT).show();
                    }
                });

        mTMDBService.getCredits(getString(R.string.apikey), actorId,
                new Callback<MDBActorCreditsList>(){

                    @Override
                    public void success(MDBActorCreditsList mdbActorCreditsList, Response response) {
                        if(mdbActorCreditsList.getAppearanceCount() > 0){
                            (findViewById(R.id.tv_label_appearances))
                                    .setVisibility(View.VISIBLE);
                            mTVAppearances.setText(mdbActorCreditsList.getFormattedAppearance());
                            mTVAppearances.setVisibility(View.VISIBLE);
                        }

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ActorDetailActivity.this, "Oups...", Toast.LENGTH_SHORT).show();
                    }
                });
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
