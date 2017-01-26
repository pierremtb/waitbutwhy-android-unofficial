package com.pierrejacquier.waitbutwhyunofficial.data;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.pierrejacquier.waitbutwhyunofficial.R;
import com.pierrejacquier.waitbutwhyunofficial.databinding.PostItemBinding;
import com.pierrejacquier.waitbutwhyunofficial.utils.ResponsiveDimens;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Scanner;

public class PostItem extends AbstractItem<PostItem, PostItem.ViewHolder> {
    public String title;
    public String link;
    public int commentsNumber;
    public String thumbnailLink;
    public boolean read = false;
    public boolean bookmarked = false;

    public PostItem(Element post) {
        this.title = post.select("h5 a").html();
        this.link = post.select("h5 a").attr("href");
        this.commentsNumber = new Scanner(post.select(".comments").html()).useDelimiter("\\D+").nextInt();
        this.thumbnailLink = post.select("div.thumbnail > a > img").attr("src");
    }

    public PostItem() {}

    public PostItem withTitle(String title) {
        this.title = title;
        return this;
    }

    public PostItem withCommentsNumber(int commentsNumber) {
        this.commentsNumber = commentsNumber;
        return this;
    }

    public PostItem withLink(String link) {
        this.link = link;
        return this;
    }

    public PostItem withBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
        return this;
    }

    public PostItem withThumbnailLink(String link) {
        this.thumbnailLink = link;
        return this;
    }

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

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
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
        ResponsiveDimens dimens = new ResponsiveDimens(viewHolder.itemView.getContext());

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(dimens.getPostItemMarginLeft(),
                            dimens.getPostItemMarginTop(),
                            dimens.getPostItemMarginLeft(),
                            dimens.getPostItemMarginTop());

        viewHolder.binding.card.setLayoutParams(params);

        Glide
            .with(viewHolder.itemView.getContext())
            .load(this.getThumbnailLink())
            .centerCrop()
            .placeholder(null)
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