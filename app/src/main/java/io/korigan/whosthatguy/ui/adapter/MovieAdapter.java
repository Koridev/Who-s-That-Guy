package io.korigan.whosthatguy.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.model.MDBMovie;
import io.korigan.whosthatguy.util.OnMovieClickListener;

/**
 * Created by guillaume on 23/04/15.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MDBMovie> mMovieList;
    private Context mContext;
    private OnMovieClickListener mMovieClickListener;

    public MovieAdapter(Context context, OnMovieClickListener omcl) {
        super();
        mContext = context;
        mMovieList = new ArrayList<MDBMovie>();
        mMovieClickListener = omcl;
    }

    public void setMovieList(List<MDBMovie> list) {
        mMovieList = list;
    }

    public void clear() {

        mMovieList = new ArrayList<MDBMovie>();
    }

    public MDBMovie getMovie(int position){
        return mMovieList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(mContext, LayoutInflater.from(mContext).inflate(R.layout.movie_row, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MovieHolder)holder).bindData(mMovieList.get(position));
        ((MovieHolder)holder).itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mMovieClickListener.onMovieClick(mMovieList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    private static class MovieHolder extends RecyclerView.ViewHolder{

        private Context mContext;
        private ViewGroup mItemView;

        public MovieHolder(Context ctx, View itemView) {
            super(itemView);
            mContext = ctx;
            mItemView = (ViewGroup) itemView;
        }

        public void bindData(MDBMovie m){
            ((TextView)mItemView.findViewById(R.id.tv_title)).setText(m.getTitle());
            ((TextView)mItemView.findViewById(R.id.tv_info)).setText(m.getReleaseDate());
            Picasso.with(mContext)
                    .load(mContext.getString(R.string.endpoint_tmdb_img)+"/w300"+m.getPosterPath())
                    .centerCrop()
                    .fit()
                    .into((ImageView)mItemView.findViewById(R.id.img_poster));
        }
    }
}