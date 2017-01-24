package com.pierrejacquier.waitbutwhyunofficial.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FooterAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.FragmentPostsBinding;
import com.pierrejacquier.waitbutwhyunofficial.items.PostItem;
import com.pierrejacquier.waitbutwhyunofficial.utils.DbHelper;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private OnFragmentInteractionListener mListener;
    private FragmentPostsBinding binding;
    private PostItem currentItem;
    private int currentPage = 1;

    private FastItemAdapter fastAdapter = new FastItemAdapter();
    private FooterAdapter<ProgressItem> footerAdapter;

    private String category;

    private DbHelper dbHelper;

    public PostsFragment() {}

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(getContext());

        category = getArguments().getString("category");

        fetchPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_posts, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchPosts();
    }

    private void fetchPosts() {
        Utils.fetchPosts(category, 1, getContext(), new Utils.PostsReceiver() {
            @Override
            public void onPostsReceived(List<PostItem> posts) {
                displayPosts(posts);
            }
        });
    }

    private void displayPosts(List<PostItem> posts) {
        fastAdapter = new FastItemAdapter();
        footerAdapter = new FooterAdapter<>();

        binding.recyclerView.setAdapter(footerAdapter.wrap(fastAdapter));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        fastAdapter.add(posts);
        fastAdapter.notifyDataSetChanged();
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<PostItem>() {
            @Override
            public boolean onClick(View v, IAdapter<PostItem> adapter, PostItem item, int position) {
                mListener.onPostSelected(item, v.findViewById(R.id.first_text_view));
                return true;
            }
        });
        fastAdapter.withItemEvent(new ClickEventHook<PostItem>() {
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof PostItem.ViewHolder) {
                    return ((PostItem.ViewHolder) viewHolder).binding.secondaryAction;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<PostItem> fastAdapter, PostItem item) {
                setCurrentItem(item);
                showPopup(v);
            }
        });

        binding.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(final int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));

                Utils.fetchPosts(category, currentPage, getContext(), new Utils.PostsReceiver() {
                    @Override
                    public void onPostsReceived(List<PostItem> posts) {
                        footerAdapter.clear();
                        fastAdapter.add(posts);
                    }
                });
            }
        });

        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.progressView.setVisibility(View.GONE);
    }

    private void setCurrentItem(PostItem item) {
        this.currentItem = item;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_post, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mark_as_read:
                dbHelper.toggleReadPost(this.currentItem.getLink());
                this.fetchPosts();
                return true;
            case R.id.add_to_reading_list:
                return true;
            case R.id.open_on_wbw_website:
                Utils.openOnWBW(this.currentItem.getLink(), getContext());
                return true;
            default:
                return false;
        }
    }

    public interface OnFragmentInteractionListener {
        void onPostSelected(PostItem post, View startView);
    }
}
