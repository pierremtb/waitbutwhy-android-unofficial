package com.pierrejacquier.waitbutwhyunofficial.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
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
import com.pierrejacquier.waitbutwhyunofficial.fragments.PostsFragment;
import com.pierrejacquier.waitbutwhyunofficial.fragments.RandomPostFragment;
import com.pierrejacquier.waitbutwhyunofficial.items.PostItem;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity implements PostsFragment.OnFragmentInteractionListener, RandomPostFragment.OnFragmentInteractionListener {

    private static final int DRAWER_EXPLORE = 0;
    private static final int DRAWER_DIVIDER = 1;
    private static final int DRAWER_SETTINGS = 2;
    private static final int DRAWER_ABOUT = 3;

    private Toolbar toolbar;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        new MaterializeBuilder().withActivity(this).withStatusBarPadding(true).build();

        toolbar = (Toolbar) binding.toolbar;
        setSupportActionBar(toolbar);

        buildDrawer();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_posts:
                        showPostsFragment("archive");
                        break;
                    case R.id.tab_minis:
                        showPostsFragment("minis");
                        break;
                    case R.id.tab_random:
                        showRandomPostFragment();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showPostsFragment(String category) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        PostsFragment fragment = new PostsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        fragment.setArguments(bundle);
        ft.replace(R.id.frame_container, fragment).commit();
    }

    private void showRandomPostFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        RandomPostFragment fragment = new RandomPostFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        ft.replace(R.id.frame_container, fragment).commit();
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
                                .withName(R.string.explore)
                                .withIdentifier(DRAWER_EXPLORE)
                                .withIcon(GoogleMaterial.Icon.gmd_explore),
                        new DividerDrawerItem().withIdentifier(DRAWER_DIVIDER),
                        new SecondaryDrawerItem()
                                .withIdentifier(DRAWER_SETTINGS)
                                .withName(R.string.settings)
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withSelectable(false),
                        new SecondaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withName(R.string.about)
                                .withIcon(GoogleMaterial.Icon.gmd_info_outline)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.d("trsuiae", position+"");
                        switch (position) {
                            case DRAWER_EXPLORE:
                                break;
                            case DRAWER_SETTINGS:
                                openSettingsActivity();
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

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .start(this);
    }

    @Override
    public void onPostSelected(PostItem post, View viewStart) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("title", post.getTitle());
        intent.putExtra("link", post.getLink());
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                viewStart,   // Starting view
//                "title"    // The String
//        );
//        //Start the Intent
//        ActivityCompat.startActivity(this, intent, options.toBundle());
        startActivity(intent);
    }
}
