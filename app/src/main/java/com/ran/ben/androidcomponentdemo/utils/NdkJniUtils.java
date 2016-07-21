package com.ran.ben.androidcomponentdemo.utils;

import android.content.Context;

/**
 * Created by yubenben
 * Date: 16-7-13.
 */
public class NdkJniUtils {
    public native int checkDexMD5(String  path,  String name,  String cachedir);
    public native int checkSign(Context context);

    static {
        System.loadLibrary("application");	//defaultConfig.ndk.moduleName
    }

}
