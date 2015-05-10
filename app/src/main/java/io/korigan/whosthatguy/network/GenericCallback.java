package io.korigan.whosthatguy.network;

import android.content.Context;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import io.korigan.whosthatguy.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guillaume on 10/05/15.
 */
public abstract class GenericCallback<T> implements Callback<T> {

    private Context mContext;

    public GenericCallback(Context ctx){
        mContext = ctx;
    }

    @Override
    public void failure(RetrofitError error) {
        if(error.getKind().equals(RetrofitError.Kind.NETWORK)){
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.error_network_title));
            builder.setMessage(mContext.getString(R.string.error_network_message));
            builder.setNeutralButton(mContext.getString(R.string.close), null);
            builder.create().show();
        }
        else if(error.getKind().equals(RetrofitError.Kind.CONVERSION)){
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.error_conversion_title));
            builder.setMessage(mContext.getString(R.string.error_conversion_message));
            builder.setNeutralButton(mContext.getString(R.string.close), null);
            builder.create().show();
        }
        else if(error.getKind().equals(RetrofitError.Kind.HTTP)){
            AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.error_http_title));
            builder.setMessage(error.getLocalizedMessage());
            builder.setNeutralButton(mContext.getString(R.string.close), null);
            builder.create().show();
        }
        else if(error.getKind().equals(RetrofitError.Kind.UNEXPECTED)){
            throw error; //Internal error, not much we can do, so crash
        }
    }
}
