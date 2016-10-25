package com.baidao.realm_jsonexample;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by hexi on 16/10/24.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
