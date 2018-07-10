package com.datable.imagepicker.image.gallery.adapter;


import com.datable.imagepicker.image.base.ItemData;

public class AlbumItemData implements ItemData {

    private long bucketid;
    private String bucketname;
    private int counter;
    private String intentPath;
    private String thumbnail;
    public String thumbnailPath;

    public long getBucketid() {
        return bucketid;
    }

    public void setBucketid(long bucketid) {
        this.bucketid = bucketid;
    }

    public String getBucketname() {
        return bucketname;
    }

    public void setBucketname(String bucketname) {
        this.bucketname = bucketname;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getIntentPath() {
        return intentPath;
    }

    public void setIntentPath(String intentPath) {
        this.intentPath = intentPath;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void plusCount() {
        counter++;
    }

}
