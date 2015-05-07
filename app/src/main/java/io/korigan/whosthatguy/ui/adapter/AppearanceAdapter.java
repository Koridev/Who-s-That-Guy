package io.korigan.whosthatguy.ui.adapter;

import android.content.Context;
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
import io.korigan.whosthatguy.model.MDBActorCreditsList;
import io.korigan.whosthatguy.model.MDBPerson;
import io.korigan.whosthatguy.ui.transformation.CircleTransformation;

/**
 * Created by guillaume on 06/05/15.
 */
public class AppearanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int PERSON_INFO_ITEM = 0;
    private static final int APPEARANCE_ITEM = 1;
    private Context mContext;
    private MDBPerson mPersonInfo;
    private List<MDBActorCreditsList.MDBActorCredit> mAppearancesList;
    private ViewGroup mParent;

    public AppearanceAdapter(Context ctx, ViewGroup parent){
        super();
        mContext = ctx;
        mParent = parent;
        mAppearancesList = new ArrayList<>();
    }

    public void setPersonInfo(MDBPerson person){
        mPersonInfo = person;
        notifyItemInserted(0);
    }

    public void setAppearanceList(List<MDBActorCreditsList.MDBActorCredit> list){
        //TODO show label appearances
        mAppearancesList = (list);
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0 && mPersonInfo != null){
            return PERSON_INFO_ITEM;
        }
        else{
            return APPEARANCE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == PERSON_INFO_ITEM){
            return new PersonInfoHolder(mContext,
                    LayoutInflater.from(mContext).inflate(R.layout.actor_info_row, mParent, false));
        }
        else if(viewType == APPEARANCE_ITEM) {
            return new AppearanceHolder(mContext,
                    LayoutInflater.from(mContext).inflate(R.layout.appearance_row, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PersonInfoHolder){
            ((PersonInfoHolder) holder).bindData(mPersonInfo);
        }
        else if(holder instanceof AppearanceHolder) {
            int listPosition = position;
            if(mPersonInfo != null){
                listPosition--;
            }
            ((AppearanceHolder) holder).bindData(mAppearancesList.get(listPosition));
        }
    }

    @Override
    public int getItemCount() {
        int count = mAppearancesList.size();
        if(mPersonInfo != null) count ++;
        return count;
    }

    private static class PersonInfoHolder extends RecyclerView.ViewHolder{
        private Context mContext;
        private ViewGroup mItemView;
        private TextView mTVBiography;
        private ImageView mImgProfile;

        public PersonInfoHolder(Context ctx, View itemView) {
            super(itemView);

            mContext = ctx;
            mItemView = (ViewGroup) itemView;
            mImgProfile = (ImageView) itemView.findViewById(R.id.img_profile);
            mTVBiography = (TextView) itemView.findViewById(R.id.tv_biography);
        }

        public void bindData(MDBPerson mdbPerson){
            if(mdbPerson.biography != null && !mdbPerson.biography.isEmpty()){
                (mItemView.findViewById(R.id.tv_label_biography))
                        .setVisibility(View.VISIBLE);
                mTVBiography.setText(mdbPerson.biography);
                mTVBiography.setVisibility(View.VISIBLE);
            }

            if(mdbPerson.profile_path != null && !mdbPerson.profile_path.isEmpty()) {
                Picasso.with(mContext)
                        .load(mContext.getString(R.string.endpoint_tmdb_img)+"/w300"+mdbPerson.profile_path)
                        .centerCrop()
                        .fit()
                        .error(R.drawable.ic_person_big)
                        .transform(new CircleTransformation())
                        .into(mImgProfile);
            }
            else{
                mImgProfile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person_big));
            }
        }
    }

    private static class AppearanceHolder extends RecyclerView.ViewHolder{

        private Context mContext;
        private ViewGroup mItemView;


        public AppearanceHolder(Context ctx, View itemView) {
            super(itemView);
            mContext = ctx;
            mItemView = (ViewGroup) itemView;
        }

        public void bindData(MDBActorCreditsList.MDBActorCredit appearance){
            ((TextView) mItemView.findViewById(R.id.tv_title))
                    .setText(appearance.getTitleWithYear());

            if(appearance.getCharacter() != null && !appearance.getCharacter().isEmpty()) {
                ((TextView) mItemView.findViewById(R.id.tv_character))
                        .setText(mContext.getString(R.string.as) + appearance.getCharacter());
            }

            if(appearance.poster_path != null && !appearance.poster_path.isEmpty()) {
                Picasso.with(mContext)
                        .load(mContext.getString(R.string.endpoint_tmdb_img) + "/w300" + appearance.poster_path)
                        .centerCrop()
                        .fit()
                        .into((ImageView) mItemView.findViewById(R.id.img_poster));

            }
            else{
                ((ImageView) mItemView.findViewById(R.id.img_poster)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_person));
            }
        }

    }

}
