package com.baidao.realmtest;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by hexi on 16/10/22.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);
    }
}
