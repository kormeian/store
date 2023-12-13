package com.arffy.server.global.paging

import org.springframework.data.domain.Page

class PagingResponse<T>(
    private var firstPage: Boolean,
    private val lastPage: Boolean,
    private val totalPage: Int,
    private val totalElements: Long,
    private val size: Int,
    private val currentPage: Int,
    private val content: List<T>,
) {


    companion object {
        fun from(page: Page<*>): PagingResponse<*> {
            return PagingResponse(
                page.isFirst,
                page.isLast,
                page.totalPages,
                page.totalElements,
                page.size,
                page.number,
                page.content
            )
        }
    }
}