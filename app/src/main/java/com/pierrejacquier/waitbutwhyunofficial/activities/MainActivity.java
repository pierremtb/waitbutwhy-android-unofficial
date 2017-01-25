package com.pierrejacquier.waitbutwhyunofficial.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialize.MaterializeBuilder;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.ActivityMainBinding;
import com.pierrejacquier.waitbutwhyunofficial.data.PostItem;
import com.pierrejacquier.waitbutwhyunofficial.utils.DbHelper;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_POSTS = 1;
    private static final int DRAWER_MINIS = 2;
    private static final int DRAWER_RANDOM = 3;
    private static final int DRAWER_BOOKMARKS = 4;
    private static final int DRAWER_DIVIDER = 5;
    private static final int DRAWER_SETTINGS = 6;
    private static final int DRAWER_ABOUT = 6;

    private Toolbar toolbar;
    private ActivityMainBinding binding;

    private DbHelper dbHelper;

    private FastItemAdapter postsAdapter = new FastItemAdapter();
    private FastItemAdapter minisAdapter = new FastItemAdapter();
    private FastItemAdapter randomAdapter = new FastItemAdapter();
    private FastItemAdapter bookmarksAdapter = new FastItemAdapter();

    private FooterAdapter<ProgressItem> postsFooterAdapter = new FooterAdapter<>();
    private FooterAdapter<ProgressItem> minisFooterAdapter = new FooterAdapter<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");

        new MaterializeBuilder()
                .withActivity(this)
                .withFullscreen(true)
                .withStatusBarPadding(true)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        buildDrawer();
        setupRecyclerViews();
        showPosts();

        binding.randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRandom();
            }
        });
    }

    private void setupRecyclerViews() {
        binding.postsRecyclerView.setAdapter(postsFooterAdapter.wrap(postsAdapter));
        binding.minisRecyclerView.setAdapter(minisFooterAdapter.wrap(minisAdapter));
        binding.randomRecyclerView.setAdapter(randomAdapter);
        binding.bookmarksRecyclerView.setAdapter(bookmarksAdapter);

        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.minisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.randomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.postsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.minisRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.randomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.bookmarksRecyclerView.setItemAnimator(new DefaultItemAnimator());

        postsAdapter.withSelectable(true);
        minisAdapter.withSelectable(true);
        randomAdapter.withSelectable(true);
        bookmarksAdapter.withSelectable(true);

        ClickEventHook<PostItem> readButtonHook = new ClickEventHook<PostItem>() {
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof PostItem.ViewHolder) {
                    return ((PostItem.ViewHolder) viewHolder).binding.share;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<PostItem> fastAdapter, PostItem item) {
                Utils.sharePost(item, getApplicationContext());
            }
        };

        ClickEventHook<PostItem> postHook = new ClickEventHook<PostItem>() {
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof PostItem.ViewHolder) {
                    return ((PostItem.ViewHolder) viewHolder).binding.cardContent;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<PostItem> fastAdapter, PostItem item) {
                onPostSelected(item, v.findViewById(R.id.primary_action));
            }
        };

        postsAdapter.withItemEvent(readButtonHook);
        minisAdapter.withItemEvent(readButtonHook);
        randomAdapter.withItemEvent(readButtonHook);
        bookmarksAdapter.withItemEvent(readButtonHook);

        postsAdapter.withItemEvent(postHook);
        minisAdapter.withItemEvent(postHook);
        randomAdapter.withItemEvent(postHook);
        bookmarksAdapter.withItemEvent(postHook);

        binding.postsList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showPosts(false);
            }
        });

        binding.minisList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showMinis(false);
            }
        });

        binding.postsRecyclerView.addOnScrollListener(getOnScrollListener(postsAdapter, postsFooterAdapter, "archive"));
        binding.minisRecyclerView.addOnScrollListener(getOnScrollListener(minisAdapter, minisFooterAdapter, "minis"));
    }

    private void displayPosts(List<PostItem> posts, SwipeRefreshLayout list, FastItemAdapter fastAdapter) {
        fastAdapter.clear();
        fastAdapter.add(posts);
        fastAdapter.notifyDataSetChanged();
        hideLoading();
        list.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        binding.postsList.setVisibility(View.GONE);
        binding.minisList.setVisibility(View.GONE);
        binding.randomList.setVisibility(View.GONE);
        binding.bookmarksList.setVisibility(View.GONE);
        binding.progressView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.progressView.setVisibility(View.GONE);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .start(this);
    }

    private void buildDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.posts)
                                .withIdentifier(DRAWER_POSTS)
                                .withIcon(GoogleMaterial.Icon.gmd_subject),
                        new PrimaryDrawerItem()
                                .withName(R.string.minis)
                                .withIdentifier(DRAWER_MINIS)
                                .withIcon(GoogleMaterial.Icon.gmd_list),
                        new PrimaryDrawerItem()
                                .withName(R.string.random)
                                .withIdentifier(DRAWER_RANDOM)
                                .withIcon(GoogleMaterial.Icon.gmd_shuffle),
                        new PrimaryDrawerItem()
                                .withName(R.string.bookmarks)
                                .withIdentifier(DRAWER_BOOKMARKS)
                                .withIcon(GoogleMaterial.Icon.gmd_collections_bookmark),
                        new DividerDrawerItem().withIdentifier(DRAWER_DIVIDER),
//                        new SecondaryDrawerItem()
//                                .withIdentifier(DRAWER_SETTINGS)
//                                .withName(R.string.settings)
//                                .withIcon(GoogleMaterial.Icon.gmd_settings)
//                                .withSelectable(false),
                        new SecondaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withName(R.string.about)
                                .withIcon(GoogleMaterial.Icon.gmd_info_outline)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.e("trsuiae", position+"");
                        switch (position) {
                            case DRAWER_POSTS:
                                showPosts();
                                break;
                            case DRAWER_MINIS:
                                showMinis();
                                break;
                            case DRAWER_RANDOM:
                                showRandom();
                                break;
                            case DRAWER_BOOKMARKS:
                                showBookmarks();
                                break;
//                            case DRAWER_SETTINGS:
//                                openSettingsActivity();
//                                break;
                            case DRAWER_ABOUT:
                                openAboutActivity();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

    private void showPosts() {
        showPosts(true);
    }

    private void showPosts(final boolean shouldCache) {
        hideNewRandomButton();
        showLoading();
        getSupportActionBar().setTitle("Posts");
        Utils.fetchPosts("archive", 1, this, new Utils.PostsReceiver() {
            @Override
            public void onPostsReceived(List<PostItem> posts) {
                Log.e("tnaiue", posts.toString());
                displayPosts(posts, binding.postsList, postsAdapter);
                if (!shouldCache) {
                    binding.postsList.setRefreshing(false);
                }
            }
        }, shouldCache);
    }

    private void showMinis() {
        showMinis(true);
    }

    private void showMinis(final boolean shouldCache) {
        hideNewRandomButton();
        showLoading();
        getSupportActionBar().setTitle("Minis");
        Utils.fetchPosts("minis", 1, this, new Utils.PostsReceiver() {
            @Override
            public void onPostsReceived(List<PostItem> posts) {
                displayPosts(posts, binding.minisList, minisAdapter);
                if (!shouldCache) {
                    binding.minisList.setRefreshing(false);
                }
            }
        }, shouldCache);
    }

    private void showRandom() {
        showNewRandomButton();
        showLoading();
        getSupportActionBar().setTitle("Random post");
        Utils.getRandomPost(this, new Utils.PostReceiver() {
            @Override
            public void onPostReceived(PostItem post) {
                List<PostItem> posts = new ArrayList<>();
                posts.add(post);
                displayPosts(posts, binding.randomList, randomAdapter);
            }
        });
    }

    private void showBookmarks() {
        hideNewRandomButton();
        showLoading();
        getSupportActionBar().setTitle("Bookmarks");
        displayPosts(dbHelper.getBookmarkedPosts(), binding.bookmarksList, bookmarksAdapter);
    }

    private void showNewRandomButton() {
        binding.randomButton.setVisibility(View.VISIBLE);
    }

    private void hideNewRandomButton() {
        binding.randomButton.setVisibility(View.GONE);
    }

    private EndlessRecyclerOnScrollListener getOnScrollListener(final FastItemAdapter fastAdapter,
                                                                final FooterAdapter footerAdapter,
                                                                final String category) {
        return new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(final int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                Utils.fetchPosts(category, currentPage, getApplicationContext(), new Utils.PostsReceiver() {
                    @Override
                    public void onPostsReceived(List<PostItem> posts) {
                        footerAdapter.clear();
                        fastAdapter.add(posts);
                    }
                });
            }
        };
    }

    private ClickEventHook<PostItem> getBookmarkButtonHook(final RecyclerView recyclerView) {
        return new ClickEventHook<PostItem>() {
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof PostItem.ViewHolder) {
                    return ((PostItem.ViewHolder) viewHolder).binding.bookmark;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<PostItem> fastAdapter, PostItem post) {
//                boolean postBookmarked = dbHelper.toggleBookmarkedPost(post);
//                post.setBookmarked(postBookmarked);
//                recyclerView.getLayoutManager().findViewByPosition(position).getRootView().
//                        binding.setPost(post);
//                binding.executePendingBindings();
//                Snackbar.make(view,
//                        getResources().getString(postBookmarked ? R.string.bookmark_added : R.string.bookmark_removed),
//                        Snackbar.LENGTH_LONG)
//                        .show();
            }
        };
    }

    public void onPostSelected(PostItem post, View viewStart) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("title", post.getTitle());
        intent.putExtra("link", post.getLink());
        intent.putExtra("thumbnail_link", post.getThumbnailLink());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                viewStart,   // Starting view
                "post"    // The String
        );
        //Start the Intent
        ActivityCompat.startActivity(this, intent, options.toBundle());
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}
