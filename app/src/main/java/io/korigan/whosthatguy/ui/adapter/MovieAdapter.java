package io.korigan.whosthatguy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.model.MDBMedia;
import io.korigan.whosthatguy.model.MDBMovie;
import io.korigan.whosthatguy.model.MDBPerson;
import io.korigan.whosthatguy.ui.activity.ActorDetailActivity;
import io.korigan.whosthatguy.util.OnMovieClickListener;

/**
 * Created by guillaume on 23/04/15.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MDBMedia> mMediaList;
    private Context mContext;
    private OnMovieClickListener mMovieClickListener;
    private int mTotalResult;

    public MovieAdapter(Context context, OnMovieClickListener omcl) {
        super();
        mContext = context;
        mMediaList = new ArrayList<MDBMedia>();
        mMovieClickListener = omcl;
    }

    public void setMediaList(List<MDBMedia> list, int totalResult) {
        mMediaList = list;
        mTotalResult = totalResult;
    }

    public void addMediaList(List<MDBMedia> list){
        mMediaList.addAll(list);
    }

    public void clear() {
        mMediaList = new ArrayList<>();
        mTotalResult = 0;
    }

    public MDBMedia getMedia(int position){
        return mMediaList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0)
            return new MovieHolder(mContext, LayoutInflater.from(mContext).inflate(R.layout.movie_row, null));
        else
            return new ActorHolder(mContext, LayoutInflater.from(mContext).inflate(R.layout.movie_row, null));
    }

    @Override
    public int getItemViewType(int position){
        if(position >= mMediaList.size() ||mMediaList.get(position) == null || mMediaList.get(position) instanceof MDBMovie){
            return 0;
        }
        else if(mMediaList.get(position) instanceof MDBPerson){
            return 1;
        }
        else return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(position >= mMediaList.size() || mMediaList.get(position) == null){
            ((MovieHolder) holder).setLoading();
        }
        final MDBMedia media = mMediaList.get(position);
        if(media instanceof MDBMovie) {
            ((MovieHolder) holder).bindData((MDBMovie)media);
            ((MovieHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieClickListener.onMovieClick((MDBMovie)media);
                }
            });
        }
        else if(media instanceof MDBPerson){
            ((ActorHolder) holder).bindData((MDBPerson)media);
            ((ActorHolder) holder).itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActorDetailActivity.class);
                    intent.putExtra(ActorDetailActivity.ACTOR_ID, media.id);
                    mContext.startActivity(intent);
                    ((ActionBarActivity)mContext).overridePendingTransition(R.transition.slide_right_in, R.transition.hold);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mTotalResult;
    }

    private static class MovieHolder extends RecyclerView.ViewHolder{

        private Context mContext;
        private ViewGroup mItemView;
        private View mInfoContainer;
        private ProgressBar mProgressBar;
        private ImageView mImgProfile;


        public MovieHolder(Context ctx, View itemView) {
            super(itemView);
            mContext = ctx;
            mItemView = (ViewGroup) itemView;
            mInfoContainer = itemView.findViewById(R.id.info_container);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mImgProfile = (ImageView)mItemView.findViewById(R.id.img_poster);
        }

        public void bindData(MDBMovie m){
            mProgressBar.setVisibility(View.INVISIBLE);
            mInfoContainer.setVisibility(View.VISIBLE);
            mImgProfile.setVisibility(View.VISIBLE);
            ((TextView)mItemView.findViewById(R.id.tv_title)).setText(m.getTitle());
            ((TextView)mItemView.findViewById(R.id.tv_info)).setText(m.getReleaseDate());
            if(m.getPosterPath() != null && !m.getPosterPath().isEmpty()) {
                Picasso.with(mContext)
                        .load(mContext.getString(R.string.endpoint_tmdb_img) + "/w300" + m.getPosterPath())
                        .centerCrop()
                        .fit()
                        .error(R.drawable.ic_movie)
                        .into(mImgProfile);
            }
            else{
                mImgProfile.setImageResource(R.drawable.ic_movie);
            }
        }

        public void setLoading(){
            mProgressBar.setVisibility(View.VISIBLE);
            mInfoContainer.setVisibility(View.INVISIBLE);
            mImgProfile.setVisibility(View.INVISIBLE);
        }
    }

    private static class ActorHolder extends RecyclerView.ViewHolder{

        private Context mContext;
        private ViewGroup mItemView;


        public ActorHolder(Context ctx, View itemView) {
            super(itemView);
            mContext = ctx;
            mItemView = (ViewGroup) itemView;

        }

        public void bindData(MDBPerson m){
            itemView.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
            ((TextView)mItemView.findViewById(R.id.tv_title)).setText(m.name);
            ((TextView)mItemView.findViewById(R.id.tv_info)).setText(m.homepage);
            Picasso.with(mContext)
                    .load(mContext.getString(R.string.endpoint_tmdb_img)+"/w300"+m.profile_path)
                    .centerCrop()
                    .fit()
                    .into((ImageView)mItemView.findViewById(R.id.img_poster));
        }
    }
}