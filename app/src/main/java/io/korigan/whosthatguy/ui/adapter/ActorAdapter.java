package io.korigan.whosthatguy.ui.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.korigan.whosthatguy.R;
import io.korigan.whosthatguy.model.MDBCast;
import io.korigan.whosthatguy.ui.activity.ActorDetailActivity;


/**
 * Created by guillaume on 23/04/15.
 */
public class ActorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MDBCast> mActorList;
    private Context mContext;

    public ActorAdapter(Context ctx){
        super();
        mContext = ctx;
        mActorList = new ArrayList<>();
    }

    public void setActorList(List<MDBCast> actorList){
        mActorList = actorList;
    }

    public void clear(){
        mActorList = new ArrayList<>();
    }

    @Override
    public ActorHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.actor_row, viewGroup, false);

        return new ActorHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        ((ActorHolder)viewHolder).bindData(mActorList.get(i));
        ((ActorHolder) viewHolder).mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActorDetailActivity.class);
                intent.putExtra(ActorDetailActivity.ACTOR_ID, mActorList.get(i).id);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation((ActionBarActivity) mContext,
//                                    ((ActorHolder) viewHolder).getImageView(),
//                                    "profile_pic");
//                    mContext.startActivity(intent, options.toBundle());
//                }
//                else{
                mContext.startActivity(intent);
                ((ActionBarActivity)mContext).overridePendingTransition(R.transition.slide_right_in, R.transition.hold);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActorList.size();
    }

    private static class ActorHolder extends RecyclerView.ViewHolder{

        private Context mContext;
        private ViewGroup mItemView;
        private ImageView mImgProfile;

        public ActorHolder(Context ctx, View itemView) {
            super(itemView);
            mContext = ctx;
            mItemView = (ViewGroup) itemView;
            mImgProfile = (ImageView) mItemView.findViewById(R.id.img_profile);
        }

        public void bindData(MDBCast castMember){
            ((TextView)mItemView.findViewById(R.id.tv_name)).setText(castMember.name);
            ((TextView)mItemView.findViewById(R.id.tv_character)).setText(castMember.character);
            if(castMember.profile_path != null && !castMember.profile_path.isEmpty()) {
                Picasso.with(mContext)
                        .load(mContext.getString(R.string.endpoint_tmdb_img) + "/w300" + castMember.profile_path)
                        .centerCrop()
                        .fit()
                        .error(R.drawable.ic_person)
                        .into(mImgProfile);
            }
            else{
                mImgProfile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person));
            }

        }

        public ImageView getImageView(){
            return mImgProfile;
        }
    }
}
