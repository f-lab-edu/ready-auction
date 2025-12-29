package com.example.moduledomain.repository.bidLogging;

import com.example.moduledomain.domain.bidLogging.BidLogging;
import com.example.moduledomain.domain.user.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidLoggingRepository extends JpaRepository<BidLogging, Long>, BidLoggingRepositoryCustom {
    List<BidLogging> findByGender(Gender gender);

}
