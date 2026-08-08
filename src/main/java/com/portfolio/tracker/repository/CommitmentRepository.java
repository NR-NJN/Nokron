package com.portfolio.tracker.repository;
import com.portfolio.tracker.model.Commitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    
}
