package com.example.readyauction.controller.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PagingResponse<T> {
    private List<T> items;
    private int pageNo; // 현재 페이지 넘버
    private int pageSize; // 페이지 사이즈
    private long totalElements; //  최초 시작점
    private int totalPages; // 페이지들 총 갯수
    private boolean first; // 마지막 페이지인지
    private boolean last; // 첫번째 페이지인지

    @Builder
    private PagingResponse(List<T> items, int pageNo, int pageSize, long totalElements,
        int totalPages, boolean first, boolean last) {
        this.items = items;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static <T> PagingResponse<T> from(List<T> items, int pageNo, int pageSize,
        Page page) {
        return PagingResponse.<T>builder()
            .items(items)
            .pageNo(pageNo)
            .pageSize(pageSize)
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
