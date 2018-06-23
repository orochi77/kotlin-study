package com.tangbba.simplegithubforjava.api.model

import com.google.gson.annotations.SerializedName

class GithubOwner(@field:SerializedName("login") val login: String,
                  @field:SerializedName("avatar_url") val avatarUrl: String)
