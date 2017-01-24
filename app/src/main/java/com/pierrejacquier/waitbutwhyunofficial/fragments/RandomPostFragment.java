package com.pierrejacquier.waitbutwhyunofficial.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.FragmentPostsBinding;
import com.pierrejacquier.waitbutwhyunofficial.databinding.FragmentRandomPostBinding;
import com.pierrejacquier.waitbutwhyunofficial.items.PostItem;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

public class RandomPostFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private OnFragmentInteractionListener mListener;
    private FragmentRandomPostBinding binding;
    private PostItem currentItem;

    public RandomPostFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_random_post, container, false);

        getRandomPost();

        binding.anotherOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRandomPost();
            }
        });

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

    private void getRandomPost() {
        binding.progressView.setVisibility(View.VISIBLE);
        binding.randomPostLayout.setVisibility(View.GONE);

        Utils.getRandomPost(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                currentItem = new PostItem().withPostPage(response);
                FastItemAdapter fastAdapter = new FastItemAdapter();

                binding.recyclerView.setAdapter(fastAdapter);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                fastAdapter.add(currentItem);
                fastAdapter.notifyDataSetChanged();
                fastAdapter.withSelectable(true);

                binding.progressView.setVisibility(View.GONE);
                binding.randomPostLayout.setVisibility(View.VISIBLE);

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
                        showPopup(v);
                    }
                });
            }
        }, getContext());
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
        void onPostSelected(PostItem item, View v);
    }
}
