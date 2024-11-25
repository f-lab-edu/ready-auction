package com.example.modulebatch.batch.job;

import com.example.moduleapi.service.auction.AuctionService;
import com.example.moduledomain.domain.auction.AuctionWinners;
import com.example.moduledomain.domain.product.Product;
import com.example.moduledomain.domain.product.ProductCondition;
import com.example.moduledomain.repository.auction.AuctionWinnersRepository;
import com.example.moduledomain.repository.product.ProductRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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

        for (Product product : products) {
            ProductCondition productCondition = ProductCondition.from(now, product);
            product.updateProductCondition(productCondition);

            if (productCondition == ProductCondition.DONE) {
                recordAuctionWinner(product);
            }
        }
        productRepository.saveAll(products);
    }

    private void recordAuctionWinner(Product product) {
        auctionService.getAuctionUserInfoByProductId(product.getId()).ifPresent(userInfo -> {
            AuctionWinners auctionWinners = AuctionWinners.builder()
                    .productId(product.getId())
                    .userId(userInfo.getFirst())
                    .price(userInfo.getSecond().intValue())
                    .build();
            auctionWinnersRepository.save(auctionWinners);
        });
    }
}
