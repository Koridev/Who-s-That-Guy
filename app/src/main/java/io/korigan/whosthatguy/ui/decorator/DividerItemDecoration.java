package io.korigan.whosthatguy.ui.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import io.korigan.whosthatguy.R;

/**
 * Created by guillaume on 29/04/15.
 */
public class DividerItemDecoration  extends RecyclerView.ItemDecoration{

    private Context mContext;

    private Paint mWhitePaint;
    private Paint mDividerPaint;

    private static final int DIVIDER_SIDES_DP = 12;
    private float mDividerHeightPx;
    private float mDividerSideSpacePx;

    public DividerItemDecoration(Context context) {
        mContext = context;

        mDividerHeightPx = 1;
        mDividerSideSpacePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DIVIDER_SIDES_DP, context.getResources().getDisplayMetrics());

        mWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(Color.WHITE);

        mDividerPaint = new Paint(mWhitePaint);
        mDividerPaint.setColor(context.getResources().getColor(R.color.grey_border));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();

        final int numBorders = childCount; //Little trick for the setting view (problem when anonymous and only 1 child

        //Draw top border
        if(numBorders >= 1) {
            final View child = parent.getChildAt(0);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int bottom = child.getTop()  + params.topMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int top = (int)(bottom - mDividerHeightPx);
            c.drawRect(left, top, right, bottom, mWhitePaint);
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }
        if(numBorders > 1) {
            //Draw dividers
            for (int i = 0; i < numBorders - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = (int) (top + mDividerHeightPx);

                c.drawRect(left, top, right, bottom, mWhitePaint);
                c.drawRect(left + mDividerSideSpacePx, top, right - mDividerSideSpacePx, bottom, mDividerPaint);
            }
        }
        //Draw bottom border
        if(numBorders >= 1){
            final View child = parent.getChildAt(numBorders - 1);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = (int) (top + mDividerHeightPx);

            c.drawRect(left, top, right, bottom, mWhitePaint);
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int top = 0;
        if(parent.getChildPosition(view) == 0){
            top = (int)(mDividerHeightPx);
        }
        outRect.set(0, top, 0, (int)(mDividerHeightPx));
    }

}
