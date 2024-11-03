package com.example.moduledomain.repository.auction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.example.moduledomain.domain.auction.Auction;

import jakarta.persistence.LockModeType;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Auction> findByProductId(Long productId);

}
