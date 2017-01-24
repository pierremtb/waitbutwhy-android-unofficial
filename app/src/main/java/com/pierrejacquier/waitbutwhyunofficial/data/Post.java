package com.pierrejacquier.waitbutwhyunofficial.data;

import android.text.Spanned;

import com.pierrejacquier.waitbutwhyunofficial.items.PostItem;

/**
 * Created by pierremtb on 23/01/2017.
 */

public class Post {
    private String title;
    private int commentsNumber;
    private String link;
    private Spanned content;
    public boolean read = false;

    public Post(PostItem item) {
        this.title = item.getTitle();
        this.commentsNumber = item.getCommentsNumber();
        this.link = item.getLink();
    }

    public Post() {}

    public Post withTitle(String title) {
        this.title = title;
        return this;
    }

    public Post withCommentsNumber(int commentsNumber) {
        this.commentsNumber = commentsNumber;
        return this;
    }

    public Post withLink(String link) {
        this.link = link;
        return this;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public Spanned getContent() {
        return content;
    }

}


