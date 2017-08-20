package com.example.fleissig.myproducthunt;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LifecycleRegistryOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MainViewModel mModel;
    private MyAdapter mAdapter;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        mSpinner = (Spinner) findViewById(R.id.spinner);
//        spinner.setAdapter(new MyAdapter(
//                toolbar.getContext(),
//                new String[]{
//                        "Section 1",
//                        "Section 2",
//                        "Section 3",
//                }));

        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                if (mAdapter != null) {
                    Category item = mAdapter.getItem(position);
                    if (item != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1,
                                        item.getSlug()))
                                .commit();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mModel.listCategories(true).observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                if (categories != null) {
                    Category[] array = new Category[categories.size()];
                    mAdapter = new MyAdapter(
                            toolbar.getContext(),
                            categories.toArray(array)
                    );
                    mSpinner.setAdapter(mAdapter);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshItems() {
        Category category = mAdapter.getItem(mSpinner.getSelectedItemPosition());
        if (category != null) {
            mModel.listPosts(category.getSlug(), true);
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }


    private static class MyAdapter extends ArrayAdapter<Category> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, Category[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            Category item = getItem(position);
            if (item != null) {
                textView.setText(item.getName());
            }

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_CATEGORY = "category";
        private MainViewModel mModel;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String category) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY, category);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            String category = getArguments().getString(ARG_CATEGORY, null);
            MutableLiveData<List<Post>> posts = mModel.listPosts(category, false);

            final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((MainActivity) getActivity()).refreshItems();
                }
            });

            posts.observe((LifecycleRegistryOwner) getActivity(), new Observer<List<Post>>() {
                @Override
                public void onChanged(@Nullable List<Post> posts) {
                    if (posts != null) {
                        recyclerView.setAdapter(new CustomAdapter(posts));
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            return rootView;
        }
    }
    
    static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        private List<Post> mDataSet;

        public CustomAdapter(List<Post> dataSet) {
            this.mDataSet = dataSet;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_item, viewGroup, false);

            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            holder.titleTextView.setText(mDataSet.get(position).getName());
            holder.descTextView.setText(mDataSet.get(position).getTagline());
            holder.upvotesTextView.setText(String.valueOf(mDataSet.get(position).getVotes_count()));
            Glide
                    .with(holder.v.getContext())
                    .load(mDataSet.get(position).getThumbnail().getImage_url())
                    .into(holder.thumbnailImageView);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            View v;
            @BindView(R.id.titleTextView) TextView titleTextView;            
            @BindView(R.id.descTextView) TextView descTextView;            
            @BindView(R.id.upvotesTextView) TextView upvotesTextView;            
            @BindView(R.id.thumbnailImageView) ImageView thumbnailImageView;            
            
            public CustomViewHolder(View view) {
                super(view);
                v = view;

                ButterKnife.bind(this, view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, DetailActivity.class);
                        int position = getAdapterPosition();
                        intent.putExtra(DetailActivity.ARG_POST_ID, mDataSet.get(position).getId());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
