package com.example.modulebatch.batch.job;

import com.example.moduleapi.service.auction.AuctionService;
import com.example.moduledomain.domain.auction.AuctionWinners;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.repository.auction.AuctionWinnersRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component("productConditionWriter")
public class ProductConditionWriter implements ItemWriter<Product> {

    private final ProductRepository productRepository;
    private final AuctionWinnersRepository auctionWinnersRepository;
    private final AuctionService auctionService;

    public ProductConditionWriter(ProductRepository productRepository, AuctionWinnersRepository auctionWinnersRepository, AuctionService auctionService) {
        this.productRepository = productRepository;
        this.auctionWinnersRepository = auctionWinnersRepository;
        this.auctionService = auctionService;
    }

    @Override
    public void write(Chunk<? extends Product> chunk) throws Exception {
        List<? extends Product> products = chunk.getItems();
        LocalDateTime now = LocalDateTime.now();
        // 경매 상태 업데이트
        for (Product product : products) {
            if (now.isBefore(product.getStartDate())) {
                product.updateProductCondition(ProductCondition.READY); // 경매 대기중
            } else if (now.isAfter(product.getCloseDate())) {
                product.updateProductCondition(ProductCondition.DONE);  // 경매 종료됨
                // Optional로 반환된 값 처리
                Optional<Pair<Long, Long>> auctionUserInfoByProductId = auctionService.getAuctionUserInfoByProductId(product.getId());
                if (auctionUserInfoByProductId.isPresent()) {
                    // Optional 값이 있을 경우 처리
                    Pair<Long, Long> userInfo = auctionUserInfoByProductId.get();

                    // 낙찰 정보를 저장
                    AuctionWinners auctionWinners = AuctionWinners.builder()
                            .productId(product.getId())
                            .userId(userInfo.getFirst())
                            .price(userInfo.getSecond().intValue())  // 가격을 int로 변환
                            .build();

                    auctionWinnersRepository.save(auctionWinners);
            } else {
                product.updateProductCondition(ProductCondition.ACTIVE);  // 경매 진행중
            }
        }
        productRepository.saveAll(products);
    }
}
