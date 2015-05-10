package io.korigan.whosthatguy.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.WhosThatGuyApp;
import io.korigan.whosthatguy.model.MDBCredits;
import io.korigan.whosthatguy.model.MDBMediaSearch;
import io.korigan.whosthatguy.model.MDBMovie;
import io.korigan.whosthatguy.network.GenericCallback;
import io.korigan.whosthatguy.network.MovieDBService;
import io.korigan.whosthatguy.ui.adapter.ActorAdapter;
import io.korigan.whosthatguy.ui.adapter.MovieAdapter;
import io.korigan.whosthatguy.ui.animation.VisibilityAnimator;
import io.korigan.whosthatguy.ui.decorator.DividerItemDecoration;
import io.korigan.whosthatguy.util.OnMovieClickListener;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class MainActivity extends AppCompatActivity implements OnMovieClickListener {

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
//    private MDBMediaSearch mMovieSearch;
    private List<MDBMediaSearch> mMediaSearches;
    private MDBCredits mCredits;

    private MovieDBService mTMDBService;

    private boolean mMoviePanelIsShown = false;
    private boolean mMovieIsLocked = false;

    private String mCurrentQuery;


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
                mETSearch.performClick();
                openKeyboard();
            }
        });

        mActorListView.setLayoutManager(new LinearLayoutManager(this));
        mActorAdapter = new ActorAdapter(this);
        mActorListView.addItemDecoration(new DividerItemDecoration(this,
                getResources().getColor(R.color.background)));
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
                mCurrentQuery = v.getText().toString();
                WhosThatGuyApp.get().sendTrackingEvent(
                        getString(R.string.category_action),
                        getString(R.string.action_movie_search),
                        mCurrentQuery);
                final String query = v.getText().toString();
                mSearchEmptyView.setVisibility(View.INVISIBLE);
                mPBMovies.setVisibility(View.VISIBLE);
                mTVMovieCount.setVisibility(View.INVISIBLE);
                closeKeyboard();
                mMediaSearches.clear();
                mTMDBService.mediaSearch(getString(R.string.apikey), query,
                        new GenericCallback<MDBMediaSearch>(MainActivity.this) {

                    @Override
                    public void success(MDBMediaSearch movieSearch, Response response) {
                        mMediaSearches.add(movieSearch);
                        setMediaSearch(movieSearch);

                        if(movieSearch.total_pages > 1){
//                            for(int i=1; i<movieSearch.total_pages; i++){
                            mTMDBService.mediaSearchByPage(getString(R.string.apikey),
                                    query,
                                    2,
                                    new GenericCallback<MDBMediaSearch>(MainActivity.this){

                                        private int mPage = 2;
                                       @Override
                                        public void success(MDBMediaSearch mdbMediaSearch, Response response) {
                                            if(mCurrentQuery.equals(query)){
                                                mMediaSearches.add(mdbMediaSearch);
                                                addMediaSearch(mdbMediaSearch);

                                                mPage++;
                                                if(mPage <= mdbMediaSearch.total_pages){
                                                    mTMDBService.mediaSearchByPage(getString(R.string.apikey),
                                                            query,
                                                            mPage,
                                                            this);
                                                }
                                            }
                                           else{
                                                Log.d("MainActivity", "current media changed while previous did not finish");
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            WhosThatGuyApp.get().sendTrackingEvent(
                                                    getString(R.string.category_error),
                                                    getString(R.string.error_network),
                                                    error.getMessage()+" (while fetching media by page)");
                                            super.failure(error);
                                        }

                                    });
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        WhosThatGuyApp.get().sendTrackingEvent(
                                getString(R.string.category_error),
                                getString(R.string.error_network),
                                error.getMessage()+" (while fetching media)");
                        mPBMovies.setVisibility(View.INVISIBLE);
                        mSearchEmptyView.setVisibility(View.VISIBLE);
                        super.failure(error);
                    }
                });
                return true;
            }
        });

        mMediaSearches = new ArrayList<>();
    }

    @Override
    protected void onStart(){
        super.onStart();
        WhosThatGuyApp.get().sendScreenView("view.main");
    }

    @Override
    public void onSaveInstanceState(Bundle b){
        b.putBoolean(PANEL_IS_OPEN, mMoviePanelIsShown);
        b.putString(MOVIE_TITLE, mETSearch.getText().toString());
        b.putBoolean(MOVIE_IS_LOCKED, mMovieIsLocked);
        if(mSelectedMovie != null){
            b.putParcelable(SELECTED_MOVIE, Parcels.wrap(MDBMovie.class, mSelectedMovie));
        }
        if(!mMediaSearches.isEmpty()){
            b.putParcelable(LAST_MOVIE_SEARCH, Parcels.wrap(mMediaSearches));
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
            lockMovie(mSelectedMovie, false);
        }
        Parcelable pMovieSearch = b.getParcelable(LAST_MOVIE_SEARCH);
        if(pMovieSearch != null){
            mMediaSearches = Parcels.unwrap(pMovieSearch);
            if(mMoviePanelIsShown && !mMediaSearches.isEmpty()){
                setMediaSearch(mMediaSearches.get(0));
                if(mMediaSearches.size() > 1){
                    for(int i=1;i< mMediaSearches.size();i++){
                        addMediaSearch(mMediaSearches.get(i));
                    }
                }
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

        WhosThatGuyApp.get().sendTrackingEvent(
                getString(R.string.category_action),
                getString(R.string.action_movie_click),
                movie.getTitle());
        mSelectedMovie = movie;
        hideMoviePanel();
        mEmptyView.setVisibility(View.INVISIBLE);
        mNoActorView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        mTMDBService.movieCredits(getString(R.string.apikey),
                movie.getMediaType(),
                movie.id,
                new GenericCallback<MDBCredits>(MainActivity.this){

                    @Override
                    public void success(MDBCredits mdbCredits, Response response) {
                        setCast(mdbCredits);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        WhosThatGuyApp.get().sendTrackingEvent(
                                getString(R.string.category_error),
                                getString(R.string.error_network),
                                error.getMessage()+" (while fetching movieCredits)");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        super.failure(error);
                    }
                });
    }

    private void setMediaSearch(MDBMediaSearch mediaSearch){
        mPBMovies.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMediaList(mediaSearch.getMediaList(), mediaSearch.total_results);
        mMovieAdapter.notifyDataSetChanged();

        if(mediaSearch.getMediaCount() > 0) {
            mTVMovieCount.setText(
                    getString(R.string.movie_count_start)+
                            mediaSearch.getMediaCount()+
                            getString(R.string.movie_count_end));
            mSearchEmptyView.setVisibility(View.INVISIBLE);

        }
        else{
            mTVMovieCount.setText(getString(R.string.no_movie));
            mSearchEmptyView.setVisibility(View.VISIBLE);
        }

        mTVMovieCount.setVisibility(View.VISIBLE);
    }

    private void addMediaSearch(MDBMediaSearch mediaSearch){
        mMovieAdapter.addMediaList(mediaSearch.getMediaList());
        mMovieAdapter.notifyDataSetChanged();
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
                .setListener(new AnimatorListenerAdapter(){
                    private int mLayerType;
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mLayerType = mMovieSearchContainer.getLayerType();
                        mMovieSearchContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mMovieSearchContainer.setLayerType(mLayerType, null);
                    }
                })
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
                .setListener(new AnimatorListenerAdapter() {
                    private int mLayerType;
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mLayerType = mMovieSearchContainer.getLayerType();
                        mMovieSearchContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mMovieSearchContainer.setLayerType(mLayerType, null);
                        cleanMoviePanel();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mMovieSearchContainer.setLayerType(mLayerType, null);
                        cleanMoviePanel();
                    }
                })
                .start();

        mMoviePanelIsShown = false;

        if(mSelectedMovie != null) {
            lockMovie(mSelectedMovie, true);
        }
    }

    private void cleanMoviePanel(){
        mTVMovieCount.setText("");
        mTVMovieCount.setVisibility(View.INVISIBLE);
        mMovieSearchContainer.setVisibility(View.INVISIBLE);
        mMovieAdapter.clear();
        mMovieAdapter.notifyDataSetChanged();
        mMediaSearches.clear();
    }

    private KeyListener mETKeyListener;
    private void lockMovie(MDBMovie movie, boolean animate){
        mETSearch.setText(movie.getTitle());
        mETSearch.setEllipsize(TextUtils.TruncateAt.END);
        mETSearch.setEnabled(false);
        mETKeyListener = mETSearch.getKeyListener();
        mETSearch.setKeyListener(null);
        if(animate) {
            VisibilityAnimator.fadeOut(mICSearch,
                    View.GONE, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            VisibilityAnimator.fadeIn(mICEdit);
                            //animate mETSearch background color
                            ((TransitionDrawable)mETSearch.getBackground()).startTransition(200);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

        }
        else{
            mICSearch.setVisibility(View.GONE);
            mICEdit.setVisibility(View.VISIBLE);
            ((TransitionDrawable)mETSearch.getBackground()).startTransition(0);
        }
        mMovieIsLocked = true;
    }


    private void unlockMovie(){
        mETSearch.setEnabled(true);
        mETSearch.setKeyListener(mETKeyListener);
        mETSearch.getEditableText().clear();

        VisibilityAnimator.fadeOut(mICEdit, View.GONE, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                VisibilityAnimator.fadeIn(mICSearch);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        //animate mETSearch background color
        ((TransitionDrawable)mETSearch.getBackground()).reverseTransition(200);
        mMovieIsLocked = false;
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mETSearch.getWindowToken(), 0);
    }

    private void openKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(mETSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
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
