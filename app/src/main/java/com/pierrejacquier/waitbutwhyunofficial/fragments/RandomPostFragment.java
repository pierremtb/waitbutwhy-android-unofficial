package com.pierrejacquier.waitbutwhyunofficial.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.FragmentPostsBinding;
import com.pierrejacquier.waitbutwhyunofficial.databinding.FragmentRandomPostBinding;
import com.pierrejacquier.waitbutwhyunofficial.items.PostItem;
import com.pierrejacquier.waitbutwhyunofficial.utils.Utils;

public class RandomPostFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private FragmentRandomPostBinding binding;
    private PostItem currentItem;

    public RandomPostFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getRandomPost(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                currentItem = new PostItem().withPostPage(response);
            }
        }, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_random_post, container, false);
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

    public interface OnFragmentInteractionListener {
    }
}
