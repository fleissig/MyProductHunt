package com.example.fleissig.myproducthunt;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {
    @Inject
    ProductHuntService service;
    private MutableLiveData<List<Category>> categories;
    private HashMap<String, MutableLiveData<List<Post>>> posts = new HashMap<>();
    
    public MainViewModel(Application application) {
        super(application);

        ((MyApplication) application).getComponent().inject(this);
    }

    public @Nullable MutableLiveData<List<Post>> listPosts(
            @Nullable final String category, boolean forceUpdate) {
        if(category == null) return null;

        MutableLiveData<List<Post>> postsLiveData = posts.get(category);
        if(postsLiveData == null) {
            postsLiveData = new MutableLiveData<>();
            posts.put(category, postsLiveData);
            forceUpdate = true;
        }

        final MutableLiveData<List<Post>> finalPostsLiveData = postsLiveData;
        if (forceUpdate) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<PostsRoot> listCall = service.listPosts(category);
                    try {
                        Response<PostsRoot> response = listCall.execute();
                        PostsRoot body = response.body();
                        if (body != null) {
                            List<Post> posts = body.getPosts();
                            finalPostsLiveData.postValue(posts);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        return postsLiveData;
    }
    
    public MutableLiveData<List<Category>> listCategories(boolean forceUpdate) {
        if (categories == null) {
            categories = new MutableLiveData<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<CategoriesRoot> listCall = service.listCategories();
                    try {
                        Response<CategoriesRoot> response = listCall.execute();
                        CategoriesRoot body = response.body();
                        if (body != null) {
                            List<Category> categories = body.getCategories();
                            MainViewModel.this.categories.postValue(categories);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        return categories;
    }
}
