package com.ran.ben.androidcomponentdemo.utils;

import android.graphics.Color;
import android.graphics.Point;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

/**
 * Created by yubenben on 15-12-15.
 */
public class ViewUtils {

    public static Point translateLocationWithOther(View view, View target) {
        Point location = new Point();

        int[] viewLocation = new int[2];
        int[] targetLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        target.getLocationInWindow(targetLocation);
        location.set(viewLocation[0] - targetLocation[0], viewLocation[1] - targetLocation[1]);

        return location;
    }

    /**
     * 对view设置涟漪效果
     *
     * @param view 需要设置涟漪效果的view
     */
    public static void applyRippleEffect(View view) {
        MaterialRippleLayout.on(view)
                .rippleColor(Color.WHITE)
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .rippleOverlay(true)
                .create();
    }
}
