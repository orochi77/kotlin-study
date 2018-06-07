package com.tangbba.simplegithubforjava.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RepoSearchResponse {

    @SerializedName("total_count")
    private int mTotalCount;
    @SerializedName("items")
    private List<GithubRepo> mItems;

    public RepoSearchResponse(int totalCount, List<GithubRepo> items) {
        mTotalCount = totalCount;
        mItems = items;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public List<GithubRepo> getItems() {
        return mItems;
    }

    public void setItems(List<GithubRepo> items) {
        mItems = items;
    }
}
