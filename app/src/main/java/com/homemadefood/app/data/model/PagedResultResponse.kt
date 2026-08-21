package com.homemadefood.app.data.model

data class PagedResultResponse<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val totalCount: Int = 0,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false
)