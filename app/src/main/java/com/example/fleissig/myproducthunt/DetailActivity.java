package com.example.fleissig.myproducthunt;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements LifecycleRegistryOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    public static final String ARG_POST_ID = "post_id";
    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.descTextView) TextView descTextView;
    @BindView(R.id.upvotesTextView) TextView upvotesTextView;
    @BindView(R.id.screenshotImageView) ImageView screenshotImageView;
    @BindView(R.id.getItButton) Button getItButton;
    private DetailViewModel mModel;

    @OnClick(R.id.getItButton)
    public void getIt(Button button) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        long postId = bundle.getLong(ARG_POST_ID);
        mModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        mModel.getPostDetails(postId).observe(this, new Observer<Post>() {
            @Override
            public void onChanged(@Nullable final Post post) {
                if (post != null) {
                    titleTextView.setText(post.getName());
                    descTextView.setText(post.getTagline());
                    upvotesTextView.setText(String.valueOf(post.getVotes_count()));

                    Glide
                            .with(getApplicationContext())
                            .load(post.getScreenshot_url().getPx300())
                            .into(screenshotImageView);

                    getItButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(post.getRedirect_url()));
                            startActivity(browserIntent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
