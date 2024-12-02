package com.example.moduleapi.service.logging;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.repository.bidLogging.BidLoggingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidLoggingService {

    private final BidLoggingRepository bidLoggingRepository;

    public BidLoggingService(BidLoggingRepository bidLoggingRepository) {
        this.bidLoggingRepository = bidLoggingRepository;
    }

    @Transactional
    public void logging(BidLogging bidLogging) {
        bidLoggingRepository.save(bidLogging);
    }
}
