package com.portfolio.tracker.repository;

import com.portfolio.tracker.model.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    List<CashFlow> findByCommitmentIdOrderByDateAsc(Long commitmentId);
}