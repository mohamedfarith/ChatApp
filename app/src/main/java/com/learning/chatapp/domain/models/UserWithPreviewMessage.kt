package com.learning.chatapp.domain.models

data class UserWithPreviewMessage(
    val userId: String,
    val name: String,
    val latestMessage: String?,
    val timestamp: Long?
)
