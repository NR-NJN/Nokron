package com.portfolio.tracker.repository;

import com.portfolio.tracker.model.Fund;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FundRepository extends JpaRepository<Fund, Long> {
}