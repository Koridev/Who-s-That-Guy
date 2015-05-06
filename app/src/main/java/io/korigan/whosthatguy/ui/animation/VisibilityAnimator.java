package io.korigan.whosthatguy.ui.animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by guillaume on 04/05/15.
 */
public class VisibilityAnimator {

    private static DecelerateInterpolator interpolator = new DecelerateInterpolator();

    public static void fadeIn(View v){
        AlphaAnimation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setDuration(200);
        fadeIn.setInterpolator(interpolator);
        v.startAnimation(fadeIn);
        v.setVisibility(View.VISIBLE);
    }
    public static void fadeOut(final View v,
                                final int visibilityAfter,
                                final Animation.AnimationListener listener){

        AlphaAnimation fadeOut = new AlphaAnimation(1,0);
        fadeOut.setDuration(200);
        fadeOut.setInterpolator(interpolator);

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                if(listener != null)
                    listener.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(visibilityAfter);
                if(listener != null)
                    listener.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(listener != null)
                    listener.onAnimationRepeat(animation);
            }
        });

        v.startAnimation(fadeOut);

    }

//    public static void translateLeft(final LinearLayout v,
//                                     final float distance){
//        AnimationSet set = new AnimationSet(true);
//        set.setFillAfter(true);
//
//        TranslateAnimation translate = new TranslateAnimation(0, -distance, 0, 0);
//        translate.setDuration(2000);
//        translate.setInterpolator(interpolator);
//
//        v.startAnimation(translate);
//
////        Animation marginAnimation = new Animation(){
////            @Override
////            protected void applyTransformation(float interpolatedTime, Transformation t){
////                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.getLayoutParams();
////                params.rightMargin = (int)(distance * interpolatedTime);
////                v.setLayoutParams(params);
////            }
////        };
////
////        marginAnimation.setDuration(2000);
////
////        v.startAnimation(marginAnimation);
//    }

//    public static void translateRight(final View v,
//                                      final float distance){
//        v.setVisibility(View.VISIBLE);
//        AnimationSet set = new AnimationSet(true);
////        set.setFillAfter(true);
//
//        TranslateAnimation translate = new TranslateAnimation(-distance, 0, 0, 0);
//        translate.setDuration(2000);
//        translate.setInterpolator(interpolator);
//
//        v.startAnimation(translate);
//    }

//    public static void popIn(View v){
//        AnimationSet set = new AnimationSet(true);
//
//        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//        alphaAnimation.setDuration(2000);
//        alphaAnimation.setInterpolator(new DecelerateInterpolator());
//
//        v.setVisibility(View.VISIBLE);
//
//
//        Log.e("VisibilityAnimator", "height: " + v.getHeight());
//        Log.e("VisibilityAnimator", "width: "+v.getWidth());
//        Log.e("VisibilityAnimator", "height: " + v.getMeasuredHeight());
//        Log.e("VisibilityAnimator", "width: "+v.getMeasuredWidth());
//        ScaleAnimation scaleAnimationGrow = new ScaleAnimation(0,1f,0,1f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimationGrow.setDuration(2000);
//        scaleAnimationGrow.setInterpolator(new DecelerateInterpolator());
//
////        ScaleAnimation scaleAnimationShrink = new ScaleAnimation(1f,0.66f,1f,0.66f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
////        scaleAnimationShrink.setStartOffset(2001);
////        scaleAnimationShrink.setDuration(1000);
////        scaleAnimationShrink.setInterpolator(new AccelerateInterpolator());
//
//        set.addAnimation(alphaAnimation);
//        set.addAnimation(scaleAnimationGrow);
////        set.addAnimation(scaleAnimationShrink);
//
//
//        v.startAnimation(set);
//
//
//    }
//
//    public static void popOut(final View v, final int visibilityAfter, final Animation.AnimationListener listener){
//        AnimationSet set = new AnimationSet(true);
//
//        AlphaAnimation animation = new AlphaAnimation(1, 0);
//        animation.setDuration(2000);
//        animation.setInterpolator(new AccelerateInterpolator());
//
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setDuration(2000);
//        scaleAnimation.setInterpolator(new AccelerateInterpolator());
//
//
//        set.addAnimation(animation);
//        set.addAnimation(scaleAnimation);
//
//        set.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                if(listener != null){
//                    listener.onAnimationStart(animation);
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if(listener != null){
//                    listener.onAnimationEnd(animation);
//                }
//                v.setVisibility(visibilityAfter);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                if(listener != null){
//                    listener.onAnimationRepeat(animation);
//                }
//            }
//        });
//
//        v.startAnimation(set);
//
//
//    }
}
