package com.example.fleissig.myproducthunt;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class DetailViewModel extends AndroidViewModel {
    @Inject
    ProductHuntService service;
    private MutableLiveData<Post> postLiveData;

    public DetailViewModel(Application application) {
        super(application);

        ((MyApplication) application).getComponent().inject(this);
    }

    public MutableLiveData<Post> getPostDetails(final long postId) {
        if (postLiveData == null) {
            postLiveData = new MutableLiveData<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<PostDetailsRoot> call = service.getPostDetails(postId);
                    try {
                        Response<PostDetailsRoot> response = call.execute();
                        PostDetailsRoot body = response.body();
                        if (body != null) {
                            Post post = body.getPost();
                            postLiveData.postValue(post);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        return postLiveData;
    }
}
