package com.salakhov.news.data.remote_data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceDto(
    @SerialName("name")
    val name: String = ""
)