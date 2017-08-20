package com.example.fleissig.myproducthunt;

import android.app.Application;

public class MyApplication extends Application {
    private DaggerComponent component;
    public DaggerComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerDaggerComponent.builder()
                .daggerModule(new DaggerModule(this)).build();
    }
}
