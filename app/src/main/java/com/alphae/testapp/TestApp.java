package com.alphae.testapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
