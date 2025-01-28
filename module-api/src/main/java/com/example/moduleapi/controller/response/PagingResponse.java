package com.example.moduleapi.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PagingResponse<T> {
    private List<T> items;
    private int pageNo;

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
