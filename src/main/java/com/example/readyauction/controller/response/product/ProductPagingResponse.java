package com.example.readyauction.controller.response.product;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.readyauction.domain.product.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductPagingResponse {
    private List<ProductFindResponse> products;
    private int pageNo; // 현재 페이지 넘버
    private int pageSize; // 페이지 사이즈
    private long totalElements; //  최초 시작점
    private int totalPages; // 페이지들 총 갯수
    private boolean first; // 마지막 페이지인지
    private boolean last; // 첫번째 페이지인지

    @Builder
    private ProductPagingResponse(List<ProductFindResponse> products, int pageNo, int pageSize, long totalElements,
        int totalPages, boolean first, boolean last) {
        this.products = products;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static ProductPagingResponse from(List<ProductFindResponse> productFindResponses, int pageNo, int pageSize,
        Page<Product> page) {
        return ProductPagingResponse.builder()
            .products(productFindResponses)
            .pageNo(pageNo)
            .pageSize(pageSize)
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
