package com.portfolio.tracker.service;

import com.portfolio.tracker.model.CashFlow;
import com.portfolio.tracker.model.Valuation;
import com.portfolio.tracker.repository.CashFlowRepository;
import com.portfolio.tracker.repository.CommitmentRepository;
import com.portfolio.tracker.repository.ValuationRepository;
import com.portfolio.tracker.util.MathUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    private record Snapshot(List<CashFlow> cashFlows, BigDecimal nav, LocalDate asOf) {}

    // NAV statements lag cash flows, so the latest reported NAV is rolled forward to the last
    // cash flow date: calls after the report date add to NAV, distributions after it come out of it.
    // NAV is floored at zero because distributions can exceed the stale carrying value when an
    // asset is sold above its mark; the excess is already captured as realised cash.
    private Snapshot snapshot(Long commitmentId) {
        List<CashFlow> cashFlows = cashFlowRepository.findByCommitmentIdOrderByDateAsc(commitmentId);
        Optional<Valuation> latest = valuationRepository.findTopByCommitmentIdOrderByReportDateDesc(commitmentId);

        if (latest.isEmpty()) {
            LocalDate asOf = cashFlows.isEmpty() ? null : cashFlows.getLast().getDate();
            return new Snapshot(cashFlows, BigDecimal.ZERO, asOf);
        }

        Valuation valuation = latest.get();
        BigDecimal nav = valuation.getNavAmount();
        LocalDate asOf = valuation.getReportDate();
        for (CashFlow cf : cashFlows) {
            if (cf.getDate().isAfter(valuation.getReportDate())) {
                nav = nav.subtract(cf.getSignedAmount());
                asOf = cf.getDate();
            }
        }
        return new Snapshot(cashFlows, nav.max(BigDecimal.ZERO), asOf);
    }

    public BigDecimal calculateMOIC(Long commitmentId) {
        Snapshot snapshot = snapshot(commitmentId);
        BigDecimal totalPaidIn = BigDecimal.ZERO;
        BigDecimal totalDistributions = BigDecimal.ZERO;

        for (CashFlow cf : snapshot.cashFlows()) {
            if (cf.getType().isContribution()) {
                totalPaidIn = totalPaidIn.add(cf.getAmount().abs());
            } else {
                totalDistributions = totalDistributions.add(cf.getAmount().abs());
            }
        }

        if (totalPaidIn.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalValue = totalDistributions.add(snapshot.nav());
        return totalValue.divide(totalPaidIn, 2, RoundingMode.HALF_UP);
    }

    public double calculateIRR(Long commitmentId) {
        Snapshot snapshot = snapshot(commitmentId);
        List<CashFlow> cashFlows = snapshot.cashFlows();
        boolean hasNav = snapshot.nav().signum() > 0;

        int size = cashFlows.size() + (hasNav ? 1 : 0);
        double[] amounts = new double[size];
        LocalDate[] dates = new LocalDate[size];

        int index = 0;
        for (CashFlow cf : cashFlows) {
            amounts[index] = cf.getSignedAmount().doubleValue();
            dates[index] = cf.getDate();
            index++;
        }

        if (hasNav) {
            amounts[index] = snapshot.nav().doubleValue();
            dates[index] = snapshot.asOf();
        }
        return MathUtils.calculateXIRR(amounts, dates) * 100.0;
    }
}
