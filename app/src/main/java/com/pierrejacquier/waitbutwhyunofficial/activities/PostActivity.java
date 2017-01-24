package com.pierrejacquier.waitbutwhyunofficial.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.data.Post;
import com.pierrejacquier.waitbutwhyunofficial.databinding.ActivityPostBinding;
import com.pierrejacquier.waitbutwhyunofficial.utils.DbHelper;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding;
    private Post post;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        post = new Post()
                .withLink(intent.getStringExtra("link"))
                .withTitle(intent.getStringExtra("title"));
        post.setRead(dbHelper.isPostRead(post.getLink()));

        binding.setPost(post);

        final Context context = this;

        Utils.getPostContent(post.getLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements contents = document.select("div.entry-content");
                contents.append("<style>img {width: 100%; height: auto}.fsb-social-bar,#social-ads{display:none}</style>");
                binding.content.loadDataWithBaseURL("", contents.html(), "text/html", "UTF-8", "");
                binding.content.setVisibility(View.VISIBLE);
                binding.progressView.setVisibility(View.GONE);
            }
        }, this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean postRead = dbHelper.toggleReadPost(post.getLink());
                post.setRead(postRead);
                binding.setPost(post);
                binding.executePendingBindings();
                Snackbar.make(view, postRead ? "Post marked as read" : "Post marked as not read", Snackbar.LENGTH_LONG)
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
                supportFinishAfterTransition();
                return true;
            case R.id.mark_as_read:
                return true;
            case R.id.add_to_reading_list:
                return true;
            case R.id.open_on_wbw_website:
                Utils.openOnWBW(post.getLink(), this);
                return true;
            default:
                return false;
        }
    }
}
