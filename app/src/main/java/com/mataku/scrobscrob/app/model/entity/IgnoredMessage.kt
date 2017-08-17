package com.mataku.scrobscrob.app.model.entity

import com.google.gson.annotations.SerializedName

data class IgnoredMessage(
        val code: String,

        @SerializedName("#text")
        val text: String
)