package com.ran.ben.androidcomponentdemo.utils;

import android.app.Application;

/**
 * Created by yubenben
 * Date: 16-7-13.
 */
public class NdkJniUtils {
    public native int getCLanguageString(Application application);

    static {
        System.loadLibrary("application");	//defaultConfig.ndk.moduleName
    }

}
