package com.pierrejacquier.waitbutwhyunofficial.items;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.PostItemBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class PostItem extends AbstractItem<PostItem, PostItem.ViewHolder> {
    public String title;
    public String link;
    public int commentsNumber;
    public String thumbnailLink;
    public boolean read = false;

    public PostItem(Element post) {
        this.title = post.select("h5 a").html();
        this.link = post.select("h5 a").attr("href");
        this.commentsNumber = new Scanner(post.select(".comments").html()).useDelimiter("\\D+").nextInt();
        this.thumbnailLink = post.select("div.thumbnail > a > img").attr("src");
    }

    public PostItem() {}

    public String getTitle() {
        return title;
    }

    public int getCommentsNumber() {
        return commentsNumber;
    }

    public String getLink() {
        return link;
    }

    public String getCommentsString() {
        return this.commentsNumber + " comments";
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public PostItem withPostPage(String response) {
        Document document = Jsoup.parse(response);
        this.title = document.select("header > h1").html();
        this.commentsNumber = 0;
        this.link = document.select("head link[rel=\"canonical\"]").attr("href");
        return this;
    }

    @Override
    public int getType() {
//        return R.id.fastadapter_sampleitem_id;
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.post_item;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List<Object> posts) {
        super.bindView(viewHolder, posts);
        viewHolder.binding.setPost(this);
        Glide
            .with(viewHolder.itemView.getContext())
            .load(this.getThumbnailLink())
            .centerCrop()
            .placeholder(R.drawable.ic_action_subject)
            .crossFade()
            .into(viewHolder.binding.primaryAction);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public PostItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = PostItemBinding.bind(view);
        }
    }
}