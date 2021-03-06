package com.pierrejacquier.waitbutwhyunofficial.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.pierrejacquier.waitbutwhyunofficial.utils.ResponsiveDimens;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_POSTS = 1;
    private static final int DRAWER_MINIS = 2;
    private static final int DRAWER_RANDOM = 3;
    private static final int DRAWER_BOOKMARKS = 4;
    private static final int DRAWER_DIVIDER = 5;
    private static final int DRAWER_ABOUT = 6;

    private Toolbar toolbar;
    private ActivityMainBinding binding;

    private ResponsiveDimens dimens;

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

        dimens = new ResponsiveDimens(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");

        materializeActivity(!dimens.isLandscape(), !dimens.isChromebook());

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

    private void materializeActivity(boolean fullScreen) {
        materializeActivity(fullScreen, true);
    }

    private void materializeActivity(boolean fullScreen, boolean statusBarPadding) {
        new MaterializeBuilder()
                .withActivity(this)
                .withFullscreen(fullScreen)
                .withStatusBarPadding(statusBarPadding)
                .withTranslucentStatusBarProgrammatically(true)
                .build();
    }

    private void setRvDimens(View view) {
        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) view.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        info.leftMarginPercent = dimens.getPostsMarginLeft();
        info.widthPercent = dimens.getPostsWidth();
        view.requestLayout();
    }

    private void setRecyclerViewsDimensions() {
        setRvDimens(binding.postsRecyclerView);
        setRvDimens(binding.minisRecyclerView);
        setRvDimens(binding.randomRecyclerView);
        setRvDimens(binding.bookmarksRecyclerView);
    }

    private void setupRecyclerViews() {
        setRecyclerViewsDimensions();
        binding.postsRecyclerView.setAdapter(postsFooterAdapter.wrap(postsAdapter));
        binding.minisRecyclerView.setAdapter(minisFooterAdapter.wrap(minisAdapter));
        binding.randomRecyclerView.setAdapter(randomAdapter);
        binding.bookmarksRecyclerView.setAdapter(bookmarksAdapter);

        binding.postsRecyclerView.setLayoutManager(new GridLayoutManager(this, dimens.getPostsColumnsCount()));
        binding.minisRecyclerView.setLayoutManager(new GridLayoutManager(this, dimens.getPostsColumnsCount()));
        binding.randomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bookmarksRecyclerView.setLayoutManager(new GridLayoutManager(this, dimens.getPostsColumnsCount()));

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

    private void displayPosts(List<PostItem> posts, SwipeRefreshLayout list, RecyclerView rv, FastItemAdapter fastAdapter) {
        fastAdapter.clear();
        fastAdapter.add(posts);
        fastAdapter.notifyDataSetChanged();
        hideLoading(list);
        rv.setVisibility(View.VISIBLE);
    }

    private void showLoading(SwipeRefreshLayout list) {
        binding.postsRecyclerView.setVisibility(View.GONE);
        binding.minisRecyclerView.setVisibility(View.GONE);
        binding.randomRecyclerView.setVisibility(View.GONE);
        binding.bookmarksRecyclerView.setVisibility(View.GONE);
        binding.postsList.setVisibility(View.GONE);
        binding.minisList.setVisibility(View.GONE);
        binding.randomList.setVisibility(View.GONE);
        binding.bookmarksList.setVisibility(View.GONE);

        list.setVisibility(View.VISIBLE);
        list.setRefreshing(true);
    }

    private void showRecyclerView(RecyclerView rv) {
        rv.setVisibility(View.VISIBLE);
    }

    private void hideLoading(SwipeRefreshLayout list) {
        list.setRefreshing(false);
    }

    private void openAboutActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription("Wait But Why (Unofficial) is an " +
                        "<a href=\"https://github.com/pierremtb/waitbutwhy-android-unofficial\">open-source app</a>, " +
                        "<b>not affiliated with waitbutwhy.com</b>")
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
                        new SecondaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withName(R.string.about)
                                .withIcon(GoogleMaterial.Icon.gmd_info_outline)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
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
        showLoading(binding.postsList);
        getSupportActionBar().setTitle("Posts");
        Utils.fetchPosts("archive", 1, this, new Utils.PostsReceiver() {
            @Override
            public void onPostsReceived(List<PostItem> posts) {
                displayPosts(posts, binding.postsList, binding.postsRecyclerView, postsAdapter);
            }
        }, shouldCache);
    }

    private void showMinis() {
        showMinis(true);
    }

    private void showMinis(final boolean shouldCache) {
        hideNewRandomButton();
        showLoading(binding.minisList);
        getSupportActionBar().setTitle("Minis");
        Utils.fetchPosts("minis", 1, this, new Utils.PostsReceiver() {
            @Override
            public void onPostsReceived(List<PostItem> posts) {
                displayPosts(posts, binding.minisList, binding.minisRecyclerView, minisAdapter);
            }
        }, shouldCache);
    }

    private void showRandom() {
        showNewRandomButton();
        showLoading(binding.randomList);
        getSupportActionBar().setTitle("Random post");
        Utils.getRandomPost(this, new Utils.PostReceiver() {
            @Override
            public void onPostReceived(PostItem post) {
                List<PostItem> posts = new ArrayList<>();
                posts.add(post);
                displayPosts(posts, binding.randomList, binding.randomRecyclerView, randomAdapter);
            }
        });
    }

    private void showBookmarks() {
        hideNewRandomButton();
        showLoading(binding.bookmarksList);
        getSupportActionBar().setTitle("Bookmarks");
        displayPosts(dbHelper.getBookmarkedPosts(), binding.bookmarksList, binding.bookmarksRecyclerView, bookmarksAdapter);
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
//                TODO: toggle bookmarked
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
                viewStart,
                "post"
        );
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

//        TODO: get and display search results
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        this.dimens = new ResponsiveDimens(this);
        setRecyclerViewsDimensions();
        materializeActivity(!dimens.isLandscape());
    }
}
