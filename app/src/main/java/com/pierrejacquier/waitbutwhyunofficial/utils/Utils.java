package com.pierrejacquier.waitbutwhyunofficial.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pierrejacquier.waitbutwhyunofficial.data.PostItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pierremtb on 23/01/2017.
 */

public class Utils {

    public static void openOnWBW(String link, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(browserIntent);
    }

    public interface PostsReceiver {
        void onPostsReceived(List<PostItem> posts);
    }

    public interface PostReceiver {
        void onPostReceived(PostItem post);
    }

    public static void fetchPosts(String category, int pageNumber, Context context, final PostsReceiver postsReceiver) {
        fetchPosts(category, pageNumber, context, postsReceiver, true);
    }

    public static void fetchPosts(String category, int pageNumber,
                                  Context context, final PostsReceiver postsReceiver,
                                  boolean shouldCache) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final DbHelper dbHelper;
        dbHelper = new DbHelper(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET ,
                "http://waitbutwhy.com/" + category + "/page/" + pageNumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Elements longPosts = document.select(".older-postlist > #widget-tab2-content > ul > li");
                        List<PostItem> postItems = new ArrayList<>();
                        for (Element longPost : longPosts) {
                            PostItem post = new PostItem(longPost);
                            post.setRead(dbHelper.isPostRead(post.getLink()));
                            postItems.add(post);
                        }
                        postsReceiver.onPostsReceived(postItems);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "Error");
                    }
                });

        stringRequest.setShouldCache(shouldCache);

        requestQueue.add(stringRequest);
    }

    public static void getPostContent(String link, Response.Listener<String> responseListener, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET ,
                link,
                responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error");
            }
        });

        requestQueue.add(stringRequest);
    }

    public static void getRandomPost(Context context, final PostReceiver postReceiver) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://waitbutwhy.com/random/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        postReceiver.onPostReceived(new PostItem().withPostPage(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error");
            }
        });

        stringRequest.setShouldCache(false);

        requestQueue.add(stringRequest);
    }
}
