package com.example.fleissig.myproducthunt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductHuntService {
    @GET("v1/categories")
    Call<CategoriesRoot> listCategories();
    @GET("v1/categories/{category}/posts")
    Call<PostsRoot> listPosts(@Path("category") String category);
    @GET("v1/posts/{post_id}")
    Call<PostDetailsRoot> getPostDetails(@Path("post_id") long postId);
}

class PostDetailsRoot {
    public Post getPost() {
        return post;
    }

    private Post post;
}

class CategoriesRoot {
    public List<Category> getCategories() {
        return categories;
    }

    private List<Category> categories;
}

class PostsRoot {
    public List<Post> getPosts() {
        return posts;
    }

    private List<Post> posts;
}

class Category {
    private int id;

    public int getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    private String slug;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}

class Post {
    private long id;
    private String name;
    private String tagline;
    private Thumbnail thumbnail;
    private ScreenshotUrl screenshot_url;
    private long votes_count;

    public String getRedirect_url() {
        return redirect_url;
    }

    private String redirect_url;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTagline() {
        return tagline;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public ScreenshotUrl getScreenshot_url() {
        return screenshot_url;
    }

    public long getVotes_count() {
        return votes_count;
    }

    class Thumbnail {
        private long id;
        private String media_type;
        private String image_url;
        private ScreenshotUrl screenshot_url;

        public long getId() {
            return id;
        }

        public String getMedia_type() {
            return media_type;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    class ScreenshotUrl {
        @SerializedName("300px")
        private String px300;

        public String getPx300() {
            return px300;
        }

        public String getPx850() {
            return px850;
        }

        @SerializedName("850px")
        private String px850;
    }
}
