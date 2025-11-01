package ygmd.kmpquiz.domain.git

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseDetails(
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("name")
    val name: String
)