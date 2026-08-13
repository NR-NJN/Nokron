package com.portfolio.tracker.service;

import com.portfolio.tracker.model.CashFlow;
import com.portfolio.tracker.model.Commitment;
import com.portfolio.tracker.model.Valuation;
import com.portfolio.tracker.repository.CashFlowRepository;
import com.portfolio.tracker.repository.CommitmentRepository;
import com.portfolio.tracker.repository.ValuationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    private final CommitmentRepository commitmentRepository;
    private final CashFlowRepository cashFlowRepository;
    private final ValuationRepository valuationRepository;

    public PerformanceService(CommitmentRepository commitmentRepository, 
                              CashFlowRepository cashFlowRepository,
                              ValuationRepository valuationRepository) {
        this.commitmentRepository = commitmentRepository;
        this.cashFlowRepository = cashFlowRepository;
        this.valuationRepository = valuationRepository;
    }

    public BigDecimal calculateMOIC(Long commitmentId) {
         
        List<CashFlow> cashFlows = cashFlowRepository.findByCommitmentIdOrderByDateAsc(commitmentId);
        BigDecimal totalPaidIn = BigDecimal.ZERO;
        BigDecimal totalDistributions = BigDecimal.ZERO;
     
        for (CashFlow cf : cashFlows) {
            if ("CALL".equalsIgnoreCase(cf.getType())) {
                 
                totalPaidIn = totalPaidIn.add(cf.getAmount().abs());
            } else if ("DISTRIBUTION".equalsIgnoreCase(cf.getType())) {
                totalDistributions = totalDistributions.add(cf.getAmount());
            }
        }
   
        if (totalPaidIn.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentNav = BigDecimal.ZERO; 
        
        Optional<Valuation> recentValuation = valuationRepository.findTopByCommitmentIdOrderByReportDateDesc(commitmentId);
        if (recentValuation.isPresent()) {
            currentNav = recentValuation.get().getNavAmount();
        }   
        BigDecimal totalValue = totalDistributions.add(currentNav);  
        return totalValue.divide(totalPaidIn, 2, RoundingMode.HALF_UP);
    }
}