package com.example.polychat

data class Post(
    val postId: String,
    val title: String,
    val content: String,
    val author: String,
    val timestamp: Long
)
