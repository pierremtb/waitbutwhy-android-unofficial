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

/**
 * Created by pierremtb on 23/01/2017.
 */

public class Utils {
    public static void openOnWBW(String link, Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(browserIntent);
    }

    public static void fetchPosts(String category, int pageNumber, Response.Listener<String> responseListener, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET ,
                "http://waitbutwhy.com/" + category + "/page/" + pageNumber,
                responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error");
            }
        });

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

    public static void getRandomPost(Response.Listener<String> responseListener, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET ,
                "http://waitbutwhy.com/random/",
                responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error");
            }
        });

        requestQueue.add(stringRequest);
    }
}
