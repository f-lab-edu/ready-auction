package com.example.readyauction.repository.auction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.readyauction.domain.auction.Auction;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
