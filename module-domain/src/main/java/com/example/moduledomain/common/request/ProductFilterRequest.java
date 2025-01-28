package com.example.moduledomain.common.request;

import com.example.moduledomain.domain.product.OrderBy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductFilterRequest {
    private OrderBy orderBy = OrderBy.START_DATE;
    private ProductFilter productFilter;
    private int pageNo = 0;
    private int pageSize = 9;

    @Builder
    public ProductFilterRequest(OrderBy orderBy, ProductFilter productFilter, int pageNo, int pageSize) {
        this.orderBy = orderBy;
        this.productFilter = productFilter;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
