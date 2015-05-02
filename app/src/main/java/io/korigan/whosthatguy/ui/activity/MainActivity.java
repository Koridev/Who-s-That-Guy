package io.korigan.whosthatguy.ui.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import org.parceler.Parcels;
import java.util.Arrays;
import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.WhosThatGuyApp;
import io.korigan.whosthatguy.model.MDBCredits;
import io.korigan.whosthatguy.model.MDBMovie;
import io.korigan.whosthatguy.model.MDBMovieSearch;
import io.korigan.whosthatguy.network.MovieDBService;
import io.korigan.whosthatguy.ui.adapter.ActorAdapter;
import io.korigan.whosthatguy.ui.adapter.MovieAdapter;
import io.korigan.whosthatguy.ui.decorator.DividerItemDecoration;
import io.korigan.whosthatguy.util.OnMovieClickListener;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class MainActivity extends ActionBarActivity implements OnMovieClickListener {

    private static final String PANEL_IS_OPEN = "PanelIsOpenend";
    private static final String MOVIE_TITLE = "MovieTitle";
    private static final String MOVIE_IS_LOCKED = "MovieIsLocked";
    private static final String SELECTED_MOVIE = "SelectedMovie";
    private static final String LAST_MOVIE_SEARCH = "LastMovieSearch";
    private static final String MOVIE_CREDITS = "MovieCredits";

    //Actors
    private ProgressBar mProgressBar;
    private RecyclerView mActorListView;
    private View mNoActorView;

    //Movies
    private ViewGroup mMovieSearchContainer;
    private ProgressBar mPBMovies;
    private RecyclerView mMovieListView;
    private View mEmptyView;
    private TextView mTVMovieCount;

    //Search container
    private EditText mETSearch;
    private View mICSearch;
    private View mICEdit;
    private View mSearchEmptyView;

    private ActorAdapter mActorAdapter;
    private MovieAdapter mMovieAdapter;

    //State
    private MDBMovie mSelectedMovie;
    private MDBMovieSearch mMovieSearch;
    private MDBCredits mCredits;

    private MovieDBService mTMDBService;

    private boolean mMoviePanelIsShown = false;
    private boolean mMovieIsLocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mMovieSearchContainer = (ViewGroup) findViewById(R.id.search_mode_container);
        mETSearch = (EditText) findViewById(R.id.et_search);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mActorListView = (RecyclerView) findViewById(R.id.list_actors);
        mEmptyView = findViewById(R.id.empty_container);
        mEmptyView.setVisibility(View.VISIBLE);
        mNoActorView = findViewById(R.id.view_no_actor);

        mPBMovies = (ProgressBar) findViewById(R.id.pb_movies);
        mMovieListView = (RecyclerView) findViewById(R.id.list_movies);
        mTVMovieCount = (TextView) findViewById(R.id.tv_movie_count);

        //Search container
        mICSearch = findViewById(R.id.ic_search);
        mICEdit = findViewById(R.id.ic_edit);
        mSearchEmptyView = findViewById(R.id.search_empty_container);

        mICEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                unlockMovie();
            }
        });

        mActorListView.setLayoutManager(new LinearLayoutManager(this));
        mActorAdapter = new ActorAdapter(this);
        mActorListView.addItemDecoration(new DividerItemDecoration(this));
        mActorListView.setAdapter(mActorAdapter);

        mMovieListView.setLayoutManager(new LinearLayoutManager(this));
        mMovieAdapter = new MovieAdapter(this, this);
        mMovieListView.addItemDecoration(new DividerItemDecoration(this));
        mMovieListView.setAdapter(mMovieAdapter);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.endpoint_tmdb))
                .setConverter(new GsonConverter(WhosThatGuyApp.get().getGson()))
                .build();

        mTMDBService = restAdapter.create(MovieDBService.class);

        mETSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMoviePanelIsShown) {
                    showMoviePanel();
                }
            }
        });

        mETSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mSearchEmptyView.setVisibility(View.INVISIBLE);
                mPBMovies.setVisibility(View.VISIBLE);
                mTVMovieCount.setVisibility(View.INVISIBLE);
                closeKeyboard();
                mTMDBService.movieSearch(getString(R.string.apikey), v.getText().toString(), new Callback<MDBMovieSearch>(){

                    @Override
                    public void success(MDBMovieSearch movieSearch, Response response) {
                        setMovieSearch(movieSearch);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainActivity.this, "Oups...", Toast.LENGTH_SHORT).show();
                        mPBMovies.setVisibility(View.INVISIBLE);
                        mSearchEmptyView.setVisibility(View.VISIBLE);
                    }
                });
                return true;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle b){
        b.putBoolean(PANEL_IS_OPEN, mMoviePanelIsShown);
        b.putString(MOVIE_TITLE, mETSearch.getText().toString());
        b.putBoolean(MOVIE_IS_LOCKED, mMovieIsLocked);
        if(mSelectedMovie != null){
            b.putParcelable(SELECTED_MOVIE, Parcels.wrap(MDBMovie.class, mSelectedMovie));
        }
        if(mMovieSearch != null){
            b.putParcelable(LAST_MOVIE_SEARCH, Parcels.wrap(MDBMovieSearch.class, mMovieSearch));
        }
        if(mCredits != null){
            b.putParcelable(MOVIE_CREDITS, Parcels.wrap(MDBCredits.class, mCredits));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle b){
        mMoviePanelIsShown = b.getBoolean(PANEL_IS_OPEN, false);
        if(mMoviePanelIsShown){
            showMoviePanel();
        }
        mETSearch.setText(b.getString(MOVIE_TITLE, ""));
        mMovieIsLocked = b.getBoolean(MOVIE_IS_LOCKED);
        Parcelable pMovie = b.getParcelable(SELECTED_MOVIE);
        if(pMovie != null) {
            mSelectedMovie = Parcels.unwrap(pMovie);
        }
        if(mMovieIsLocked && mSelectedMovie != null){
            lockMovie(mSelectedMovie);
        }
        Parcelable pMovieSearch = b.getParcelable(LAST_MOVIE_SEARCH);
        if(pMovieSearch != null){
            mMovieSearch = Parcels.unwrap(pMovieSearch);
            if(mMoviePanelIsShown){
                setMovieSearch(mMovieSearch);
            }
        }

        Parcelable pCredits = b.getParcelable(MOVIE_CREDITS);
        if(pCredits != null){
            mCredits = Parcels.unwrap(pCredits);
            setCast(mCredits);
        }
    }

    @Override
    public void onMovieClick(MDBMovie movie) {
        closeKeyboard();
        mSelectedMovie = movie;
        hideMoviePanel();
        mEmptyView.setVisibility(View.INVISIBLE);
        mNoActorView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        mTMDBService.movieCredits(getString(R.string.apikey),
                movie.getMediaType(),
                movie.id,
                new Callback<MDBCredits>(){

                    @Override
                    public void success(MDBCredits mdbCredits, Response response) {
                        setCast(mdbCredits);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainActivity.this, "Oups...", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setMovieSearch(MDBMovieSearch movieSearch){
        mMovieSearch = movieSearch;
        mPBMovies.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovieList(Arrays.asList(movieSearch.results));
        mMovieAdapter.notifyDataSetChanged();

        if(movieSearch.total_results > 0) {
            mTVMovieCount.setText(
                    getString(R.string.movie_count_start)+
                            movieSearch.total_results+
                            getString(R.string.movie_count_end));
            mSearchEmptyView.setVisibility(View.INVISIBLE);

        }
        else{
            mTVMovieCount.setText(getString(R.string.no_movie));
            mSearchEmptyView.setVisibility(View.VISIBLE);
        }

        mTVMovieCount.setVisibility(View.VISIBLE);
    }

    private void setCast(MDBCredits credits){
        mCredits = credits;
        mActorAdapter.setActorList(Arrays.asList(credits.cast));
        mActorAdapter.notifyDataSetChanged();
        mEmptyView.setVisibility(View.INVISIBLE);

        if(credits.hasActors()) {
            mNoActorView.setVisibility(View.INVISIBLE);
        }
        else{
            mNoActorView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showMoviePanel(){
        mMovieSearchContainer.setVisibility(View.VISIBLE);
        if(mMovieAdapter.getItemCount() <= 0) {
            mSearchEmptyView.setVisibility(View.VISIBLE);
        }
        mMovieSearchContainer.animate()
                .translationY(0)
                .alpha(1)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null)
                .start();

        mMoviePanelIsShown = true;
    }

    private void hideMoviePanel(){
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        mMovieSearchContainer.animate()
                .translationY(p.y)
                .alpha(0)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cleanMoviePanel();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        cleanMoviePanel();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                })
                .start();

        mMoviePanelIsShown = false;

        if(mSelectedMovie != null) {
            lockMovie(mSelectedMovie);
        }
    }

    private void cleanMoviePanel(){
        mTVMovieCount.setText("");
        mTVMovieCount.setVisibility(View.INVISIBLE);
        mMovieSearchContainer.setVisibility(View.INVISIBLE);
        mMovieAdapter.clear();
        mMovieAdapter.notifyDataSetChanged();

        mMovieSearch = null;
    }

    private void lockMovie(MDBMovie movie){
//        mETSearch.setEllipsize(TextUtils.TruncateAt.END);
//        mETSearch.getEditableText().clear();
//        mETSearch.getEditableText().append(movie.getTitle());
        mETSearch.setText(movie.getTitle());
//        mETSearch.clearComposingText();
//        mETSearch.setSelection(0);
        mETSearch.setEnabled(false);

        mICEdit.setVisibility(View.VISIBLE);
        mICSearch.setVisibility(View.INVISIBLE);

        mMovieIsLocked = true;
    }


    private void unlockMovie(){
        mETSearch.setEnabled(true);
        mETSearch.getEditableText().clear();

        mICEdit.setVisibility(View.GONE);
        mICSearch.setVisibility(View.VISIBLE);

        mMovieIsLocked = false;
    }



    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mETSearch.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
            overridePendingTransition(R.transition.slide_bottom_in, R.transition.hold);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(mMoviePanelIsShown){
            hideMoviePanel();
            if(mSelectedMovie == null) {
                mETSearch.getEditableText().clear();
            }
        }
        else{
            super.onBackPressed();
        }
    }


}