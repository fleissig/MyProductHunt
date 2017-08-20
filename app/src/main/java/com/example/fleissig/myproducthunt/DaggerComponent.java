package com.example.fleissig.myproducthunt;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DaggerModule.class})
public interface DaggerComponent {
    void inject(MainViewModel model);
    void inject(DetailViewModel model);
}
