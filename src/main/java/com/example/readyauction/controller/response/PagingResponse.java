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

    @Builder
    private PagingResponse(List<T> items, int pageNo, int pageSize) {
        this.items = items;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public static <T> PagingResponse<T> from(List<T> items, int pageNo, int pageSize) {
        return PagingResponse.<T>builder()
            .items(items)
            .pageNo(pageNo)
            .pageSize(pageSize)
            .build();
    }
}
