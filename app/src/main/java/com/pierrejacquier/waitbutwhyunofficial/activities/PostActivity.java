package com.pierrejacquier.waitbutwhyunofficial.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.mikepenz.materialize.MaterializeBuilder;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.ActivityPostBinding;
import com.pierrejacquier.waitbutwhyunofficial.data.PostItem;
import com.pierrejacquier.waitbutwhyunofficial.utils.DbHelper;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding;
    private PostItem post;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new MaterializeBuilder()
                .withActivity(this)
                .withFullscreen(true)
                .withStatusBarPadding(true)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        Intent intent = getIntent();
        post = new PostItem()
                .withLink(intent.getStringExtra("link"))
                .withTitle(intent.getStringExtra("title"))
                .withThumbnailLink(intent.getStringExtra("thumbnail_link"));
        post.setRead(dbHelper.isPostRead(post.getLink()));
        post.setBookmarked(dbHelper.isPostBookmarked(post));

        binding.setPost(post);

        binding.backgroundImage.setColorFilter(Color.argb(150, 0, 0, 0));

        Glide.with(this)
            .load(post.getThumbnailLink())
            .centerCrop()
            .placeholder(null)
            .crossFade()
            .into(binding.backgroundImage);

        Utils.getPostContent(post.getLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements contents = document.select("div.entry-content");
                contents.append(
                        "<style>" +
                            "img {width: 100%; height: auto}" +
                            ".fsb-social-bar,#social-ads{display:none}" +
                            "a {color: " + Utils.getHexColor(R.color.colorAccent, getApplicationContext()) + "}" +
                        "</style>");
                binding.content.loadDataWithBaseURL("", contents.html(), "text/html", "UTF-8", "");
                binding.content.setVisibility(View.VISIBLE);
                binding.progressView.setVisibility(View.GONE);
            }
        }, this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean postBookmarked = dbHelper.toggleBookmarkedPost(post);
                post.setBookmarked(postBookmarked);
                binding.setPost(post);
                binding.executePendingBindings();
                Snackbar.make(view,
                            getResources().getString(postBookmarked ? R.string.bookmark_added : R.string.bookmark_removed),
                            Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.content.setVisibility(View.GONE);
                binding.backgroundImage.setColorFilter(null);
                supportFinishAfterTransition();
                return true;
            case R.id.share:
                Utils.sharePost(post, this);
                return true;
            case R.id.open_on_wbw_website:
                Utils.openOnWBW(post.getLink(), this);
                return true;
            default:
                return false;
        }
    }
}
