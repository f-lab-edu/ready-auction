package com.example.moduledomain.repository.auction;

import com.example.moduledomain.domain.auction.AuctionWinners;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionWinnersRepository extends JpaRepository<AuctionWinners, Long> {
    boolean existsByProductId(Long productId);
}
