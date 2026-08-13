package com.portfolio.tracker.bootstrap;

import com.portfolio.tracker.model.Fund;
import com.portfolio.tracker.model.Valuation;
import com.portfolio.tracker.model.Commitment;
import com.portfolio.tracker.model.CashFlow;
import com.portfolio.tracker.repository.FundRepository;
import com.portfolio.tracker.repository.CommitmentRepository;
import com.portfolio.tracker.repository.CashFlowRepository;
import com.portfolio.tracker.repository.ValuationRepository;
import com.portfolio.tracker.service.PerformanceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {
    
    private final ValuationRepository valuationRepository;
    private final FundRepository fundRepository;
    private final CommitmentRepository commitmentRepository;
    private final CashFlowRepository cashFlowRepository;
    private final PerformanceService performanceService;

    // Injecting all three required repositories
    public DataSeeder(FundRepository fundRepository, CommitmentRepository commitmentRepository, 
                      CashFlowRepository cashFlowRepository, ValuationRepository valuationRepository,
                      PerformanceService performanceService) {
        this.fundRepository = fundRepository;
        this.commitmentRepository = commitmentRepository;
        this.cashFlowRepository = cashFlowRepository;
        this.valuationRepository = valuationRepository;
        this.performanceService = performanceService;
    }

    @Override
    public void run(String... args) throws Exception {
        
        Fund fundA = new Fund();
        fundA.setName("Sequoia Capital Fund IX");
        fundA.setAssetClass("Private Equity");
        fundRepository.save(fundA);

        
        Commitment myCommitment = new Commitment();
        myCommitment.setFund(fundA);
        myCommitment.setCommitmentAmount(new BigDecimal("10000000"));
        commitmentRepository.save(myCommitment);

        CashFlow call1 = new CashFlow();
        call1.setCommitment(myCommitment);
        call1.setDate(LocalDate.of(2021, 1, 15));
        call1.setAmount(new BigDecimal("-2000000"));
        call1.setType("CALL");

        CashFlow call2 = new CashFlow();
        call2.setCommitment(myCommitment);
        call2.setDate(LocalDate.of(2022, 6, 1));
        call2.setAmount(new BigDecimal("-3000000"));
        call2.setType("CALL");

        CashFlow distribution1 = new CashFlow();
        distribution1.setCommitment(myCommitment);
        distribution1.setDate(LocalDate.of(2024, 12, 1));
        distribution1.setAmount(new BigDecimal("8000000"));
        distribution1.setType("DISTRIBUTION");

        cashFlowRepository.saveAll(Arrays.asList(call1, call2, distribution1));

        Valuation q3Valuation = new Valuation();
        q3Valuation.setCommitment(myCommitment);
        q3Valuation.setReportDate(LocalDate.of(2024, 9, 30));
        q3Valuation.setNavAmount(new BigDecimal("5000000")); 
        valuationRepository.save(q3Valuation);

        System.out.println("injected successfully.");
        BigDecimal moic = performanceService.calculateMOIC(myCommitment.getId());
        System.out.println("MOIC " + moic + "x");
    }
}