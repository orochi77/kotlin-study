package com.tangbba.simplegithubforjava.api.model

import com.google.gson.annotations.SerializedName

class RepoSearchResponse(@field:SerializedName("total_count") val totalCount: Int,
                         @field:SerializedName("items") val items: List<GithubRepo>)
