package com.pierrejacquier.waitbutwhyunofficial.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.pierrejacquier.waitbutwhyunofficial.R;

public class ResponsiveDimens {
    public float postsMarginLeft;
    public float postsWidth;
    public int postsColumnsCount;
    public boolean tablet;
    public boolean landscape;
    public int postItemMarginLeft;
    public int postItemMarginTop;

    private float dpMultiplier;

    public ResponsiveDimens(Context context) {
        this.tablet = Utils.isTablet(context);
        this.dpMultiplier = this.convertDpToPixel((float) 1, context);
        this.landscape = context.getResources().getBoolean(R.bool.is_landscape);

        if (this.tablet || this.landscape) {
            this.postsMarginLeft = (float) 0.1;
            this.postsWidth = (float) 0.8;
            this.postsColumnsCount = 2;
            this.postItemMarginLeft = dpToPx(4);
            this.postItemMarginTop = dpToPx(4);
        } else {
            this.postsMarginLeft = (float) 0;
            this.postsWidth = (float) 1;
            this.postsColumnsCount = 1;
            this.postItemMarginLeft = dpToPx(8);
            this.postItemMarginTop = dpToPx(4);
        }
    }

    public float getPostsMarginLeft() {
        return postsMarginLeft;
    }

    public float getPostsWidth() {
        return postsWidth;
    }

    public int getPostsColumnsCount() {
        return postsColumnsCount;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public boolean isTablet() {
        return tablet;
    }

    public boolean isChromebook() {
        return Build.MODEL.toUpperCase().contains("chromebook".toUpperCase());
    }

    public int getPostItemMarginLeft() {
        return postItemMarginLeft;
    }

    public int getPostItemMarginTop() {
        return postItemMarginTop;
    }

    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public int dpToPx(int dp) {
        return (int) (dp * this.dpMultiplier);
    }
}
