package com.portfolio.tracker.repository;
import com.portfolio.tracker.model.Valuation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValuationRepository extends JpaRepository<Valuation, Long> {
    Optional<Valuation> findTopByCommitmentIdOrderByReportDateDesc(Long commitmentId);
}
