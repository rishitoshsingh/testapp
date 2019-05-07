package com.alphae.testapp;

public class User {

    String mPhoneNumber;
    String mUploadedUrl;
    Integer mVisitCount;

    public User() {

    }

    public User(String mPhoneNumber, String mUploadedUrl, Integer mVisitCount) {
        this.mPhoneNumber = mPhoneNumber;
        this.mUploadedUrl = mUploadedUrl;
        this.mVisitCount = mVisitCount;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public String getmUploadedUrl() {
        return mUploadedUrl;
    }

    public Integer getmVisitCount() {
        return mVisitCount;
    }

    public void updateVisit() {
        this.mVisitCount++;
    }
}