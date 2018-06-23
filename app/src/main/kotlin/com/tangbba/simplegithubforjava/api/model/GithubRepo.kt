package com.tangbba.simplegithubforjava.api.model

import com.google.gson.annotations.SerializedName

class GithubRepo(@field:SerializedName("name") val name: String,
                 @field:SerializedName("full_name") val fullName: String,
                 @field:SerializedName("owner") val owner: GithubOwner,
                 @field:SerializedName("description") val description: String?,
                 @field:SerializedName("language") val language: String?,
                 @field:SerializedName("updated_at") val updatedAt: String,
                 @field:SerializedName("stargazers_count") val stars: Int)
