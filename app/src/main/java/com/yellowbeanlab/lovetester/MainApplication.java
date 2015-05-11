package com.yellowbeanlab.lovetester;

/**
 * Created by jirawuts on 4/27/15 AD.
 */

import android.app.Application;

import com.facebook.FacebookSdk;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

}
