package me.alex.youtubedownloader.models;

import com.github.kiulian.downloader.model.search.SearchResultItem;

import javax.swing.*;

public class YoutubeResult {

    private final SearchResultItem item;
    private final ImageIcon icon;

    public YoutubeResult(SearchResultItem item, ImageIcon icon) {
        this.item = item;
        this.icon = icon;
    }

    public String getTitle() {
        return item.title();
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public SearchResultItem getItem() {
        return item;
    }

    //get corresponding cellrenderer to this result
}
