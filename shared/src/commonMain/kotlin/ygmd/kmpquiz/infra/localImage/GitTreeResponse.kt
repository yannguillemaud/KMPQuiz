package ygmd.kmpquiz.infra.localImage

import kotlinx.serialization.Serializable

@Serializable
data class GitTreeResponse(
    val sha: String,
    val url: String,
    val tree: List<GitTreeNode>,
    val truncated: Boolean
)

@Serializable
data class GitTreeNode(
    val path: String,
    val mode: String, // unused, maybe use @JsonIgnoreUnknown
    val type: String, // "blob" for file, "tree" for dir
    val sha: String,
    val size: Int? = null,
    val url: String
)