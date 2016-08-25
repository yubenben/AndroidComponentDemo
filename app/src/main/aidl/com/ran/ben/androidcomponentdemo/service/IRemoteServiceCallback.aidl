// IRemoteServiceCallback.aidl
package com.ran.ben.androidcomponentdemo.service;

// Declare any non-default types here with import statements

oneway interface IRemoteServiceCallback {
    /**
     * Called when the service has a new value for you.
     */
    void valueChanged(int value);
}
