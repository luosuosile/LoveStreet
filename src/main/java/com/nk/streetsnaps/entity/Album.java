package com.nk.streetsnaps.entity;

import java.util.List;

public class Album {

    private String id;
    private String name;

    private String isPutAway;
    private String picsNum;
    private String firstPicture;


    /****************************************************************************************************/
    private List<String> tag;
    private Integer commentAmount;
    private Integer favoriteAmount;
    private Integer readAmount;
    private Integer praiseAmount;
    private Integer pictureAmount;

    public String getPicsNum() {
        return picsNum;
    }

    public void setPicsNum(String picsNum) {
        this.picsNum = picsNum;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getIsPutAway() {
        return isPutAway;
    }

    public void setIsPutAway(String isPutAway) {
        this.isPutAway = isPutAway;
    }

    public Integer getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(Integer commentAmount) {
        this.commentAmount = commentAmount;
    }

    public Integer getFavoriteAmount() {
        return favoriteAmount;
    }

    public void setFavoriteAmount(Integer favoriteAmount) {
        this.favoriteAmount = favoriteAmount;
    }

    public Integer getReadAmount() {
        return readAmount;
    }

    public void setReadAmount(Integer readAmount) {
        this.readAmount = readAmount;
    }

    public Integer getPraiseAmount() {
        return praiseAmount;
    }

    public void setPraiseAmount(Integer praiseAmount) {
        this.praiseAmount = praiseAmount;
    }

    public Integer getPictureAmount() {
        return pictureAmount;
    }

    public void setPictureAmount(Integer pictureAmount) {
        this.pictureAmount = pictureAmount;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
