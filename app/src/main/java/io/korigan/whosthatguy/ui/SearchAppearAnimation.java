package io.korigan.whosthatguy.ui;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by guillaume on 30/04/15.
 */
public class SearchAppearAnimation extends AnimationSet {

    public SearchAppearAnimation(ActionBarActivity context) {
        super(true);

        setFillAfter(true);

        Point p = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(p);
        TranslateAnimation anim =
                new TranslateAnimation(0, 0, p.y, 120);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
    }
}
