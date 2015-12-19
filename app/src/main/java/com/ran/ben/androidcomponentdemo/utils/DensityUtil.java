/**
* @project: 58bangbang
* @file: DensityUtil.java
* @date: 2014年10月8日 下午4:52:53
* @copyright: 2014  58.com Inc.  All rights reserved. 
*/
package com.ran.ben.androidcomponentdemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;


public class DensityUtil {


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }  
  


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

    public static int gettDisplayWidth(Context ctx){
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        return width;
    }

    public static int gettDisplayHeight(Context ctx){
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels; // 屏幕宽度（像素）
        return height;
    }

    public static void printDisplayMetrics(Context ctx){
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        int height = metric.heightPixels; // 屏幕高度（像素）
        float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240 / 320 / 480 ）
    }

    public static float getDeviceDensity(Context ctx) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }

    /**
     * 获取status bar 高度
     */
    public static int getStatusBarHeight(Context ctx) {
        int statusBarHeight = 0;
        try {
            /**
             * 通过反射机制获取StatusBar高度
             */
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int height = Integer.parseInt(field.get(object).toString());
            /**
             * 设置StatusBar高度
             */
            statusBarHeight = ctx.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusBarHeight;
    }

    public static float range(int percentage, float start, float end) {
        return (end - start) * (float)percentage / 100.0F + start;
    }
}
