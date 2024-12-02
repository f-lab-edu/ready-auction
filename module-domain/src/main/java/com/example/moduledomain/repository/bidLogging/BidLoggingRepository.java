package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidLoggingRepository extends JpaRepository<BidLogging, Long> {
}
