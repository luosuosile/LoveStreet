package com.nk.streetsnaps.entity;


import java.util.Date;

public class SubComment {
    private String id;
    private String userId;
    private String content;
    private String parentCommentId;
    private String atCommentId;
    private Date createTime;

    //
    private String nickname;
    private String atNickname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getAtCommentId() {
        return atCommentId;
    }

    public void setAtCommentId(String atCommentId) {
        this.atCommentId = atCommentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAtNickname() {
        return atNickname;
    }

    public void setAtNickname(String atNickname) {
        this.atNickname = atNickname;
    }
}
