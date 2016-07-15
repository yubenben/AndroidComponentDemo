package com.ran.ben.androidcomponentdemo.utils;

import android.app.Application;

/**
 * Created by yubenben
 * Date: 16-7-13.
 */
public class NdkJniUtils {
    public native int checkDexMD5(Application application, String file);
    public native int readFromAssetsLibzip(String  path,  String name,  String cachedir);

    static {
        System.loadLibrary("application");	//defaultConfig.ndk.moduleName
    }

}
