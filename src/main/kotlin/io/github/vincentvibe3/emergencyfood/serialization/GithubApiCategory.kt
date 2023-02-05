package io.github.vincentvibe3.emergencyfood.serialization

import kotlinx.serialization.Serializable

@Serializable
data class GithubApiCategory(
    val type:String,
    val name:String,
)
