package com.example.swonlinelectureapp;

public class SearchData {
    String videoId;
    String title;
    String url;
    String publishedAt;

    public SearchData(String videoId, String title, String url, String publishedAt) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    //Video ID
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    //제목
    public String getTitle() {
        return title;
    }

    //URL
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //날짜
    public String getPublishedAt() {
        return publishedAt;
    }
}
