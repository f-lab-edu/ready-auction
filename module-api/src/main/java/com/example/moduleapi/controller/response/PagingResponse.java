package com.example.moduleapi.controller.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PagingResponse<T> {
    private List<T> items;
    private int pageNo; // 현재 페이지 넘버

    @Builder
    private PagingResponse(List<T> items, int pageNo) {
        this.items = items;
        this.pageNo = pageNo;
    }

    public static <T> PagingResponse<T> from(List<T> items, int pageNo) {
        return PagingResponse.<T>builder()
            .items(items)
            .pageNo(pageNo)
            .build();
    }
}
