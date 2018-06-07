package com.tangbba.simplegithubforjava.api.model;

import com.google.gson.annotations.SerializedName;

public class GithubRepo {

    @SerializedName("name")
    private String mName;

    @SerializedName("full_name")
    private String mFullName;

    @SerializedName("owner")
    private GithubOwner mOwner;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("language")
    private String mLanguage;

    @SerializedName("updated_at")
    private String mUpdatedAt;

    @SerializedName("stargazers_count")
    private int mStars;

    public GithubRepo(String name,
                      String fullName,
                      GithubOwner owner,
                      String description,
                      String language,
                      String updatedAt,
                      int stars) {
        mName = name;
        mFullName = fullName;
        mOwner = owner;
        mDescription = description;
        mLanguage = language;
        mUpdatedAt = updatedAt;
        mStars = stars;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public GithubOwner getOwner() {
        return mOwner;
    }

    public void setOwner(GithubOwner owner) {
        mOwner = owner;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public int getStars() {
        return mStars;
    }

    public void setStars(int stars) {
        mStars = stars;
    }
}
