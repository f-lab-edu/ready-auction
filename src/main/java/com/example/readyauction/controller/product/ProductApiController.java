package com.example.readyauction.controller.product;

import com.example.readyauction.annotaion.LoginUser;
import com.example.readyauction.controller.request.product.ProductSaveRequest;
import com.example.readyauction.controller.request.product.ProductUpdateRequest;
import com.example.readyauction.controller.response.product.ProductFindResponse;
import com.example.readyauction.controller.response.product.ProductResponse;
import com.example.readyauction.domain.user.User;
import com.example.readyauction.service.product.ProductFacade;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {

    private final ProductFacade productFacade;

    public ProductApiController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @PostMapping
    public ProductResponse enroll(
            @LoginUser User user,
            @RequestPart(name = "product") ProductSaveRequest productSaveRequest,
            @RequestPart(name = "Images") List<MultipartFile> files) {
        ProductResponse productSaveResponse = productFacade.enroll(user, productSaveRequest, files);
        return productSaveResponse;
    }

    @GetMapping("/{id}")
    public ProductFindResponse findById(@PathVariable Long id) {
        ProductFindResponse productFindResponse = productFacade.findById(id);
        return productFindResponse;
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @LoginUser User user,
            @PathVariable Long id,
            @RequestPart(name = "product") ProductUpdateRequest productUpdateRequest,
            @RequestPart(name = "Images") List<MultipartFile> files) {
        ProductResponse ProductUpdateResponse = productFacade.update(user, id, productUpdateRequest, files);
        return ProductUpdateResponse;
    }

    @DeleteMapping("/{id}")
    public ProductResponse delete(@LoginUser User user, @PathVariable Long id) {
        ProductResponse ProductDeleteResponse = productFacade.delete(user, id);
        return ProductDeleteResponse;
    }
}
