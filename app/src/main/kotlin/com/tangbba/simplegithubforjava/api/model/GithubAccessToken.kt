package com.tangbba.simplegithubforjava.api.model

import com.google.gson.annotations.SerializedName

class GithubAccessToken(@field:SerializedName("access_token") val accessToken: String,
                        @field:SerializedName("scope") val scope: String,
                        @field:SerializedName("token_type") val tokenType: String)
