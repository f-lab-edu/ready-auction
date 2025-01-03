package com.example.moduledomain.repository.bidLogging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.user.Gender;

@Repository
public interface BidLoggingRepository extends JpaRepository<BidLogging, Long> {
    List<BidLogging> findByGender(Gender gender);

}
