package com.portfolio.tracker.repository;
import com.portfolio.tracker.model.Valuation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValuationRepository extends JpaRepository<Valuation, Long> {
    
}
