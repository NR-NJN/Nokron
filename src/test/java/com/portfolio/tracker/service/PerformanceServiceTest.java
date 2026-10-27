package com.portfolio.tracker.service;

import com.portfolio.tracker.model.CashFlow;
import com.portfolio.tracker.model.CashFlowType;
import com.portfolio.tracker.model.Valuation;
import com.portfolio.tracker.repository.CashFlowRepository;
import com.portfolio.tracker.repository.CommitmentRepository;
import com.portfolio.tracker.repository.ValuationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PerformanceServiceTest {

    private static final long ID = 1L;

    private CashFlowRepository cashFlows;
    private ValuationRepository valuations;
    private PerformanceService service;

    @BeforeEach
    void setUp() {
        cashFlows = mock(CashFlowRepository.class);
        valuations = mock(ValuationRepository.class);
        service = new PerformanceService(mock(CommitmentRepository.class), cashFlows, valuations);
    }

    private static CashFlow flow(CashFlowType type, String amount, LocalDate date) {
        CashFlow cf = new CashFlow();
        cf.setType(type);
        cf.setAmount(new BigDecimal(amount));
        cf.setDate(date);
        return cf;
    }

    private void given(List<CashFlow> flows, Valuation valuation) {
        when(cashFlows.findByCommitmentIdOrderByDateAsc(ID)).thenReturn(flows);
        when(valuations.findTopByCommitmentIdOrderByReportDateDesc(ID)).thenReturn(Optional.ofNullable(valuation));
    }

    private static Valuation nav(String amount, LocalDate date) {
        Valuation v = new Valuation();
        v.setNavAmount(new BigDecimal(amount));
        v.setReportDate(date);
        return v;
    }

    @Test
    void distributionAfterValuationIsTakenOutOfNav() {
        given(List.of(
                flow(CashFlowType.CALL, "-5000000", LocalDate.of(2021, 1, 1)),
                flow(CashFlowType.DISTRIBUTION, "2000000", LocalDate.of(2024, 12, 1))),
                nav("5000000", LocalDate.of(2024, 9, 30)));

        assertEquals(new BigDecimal("1.00"), service.calculateMOIC(ID));
    }

    @Test
    void callAfterValuationIsAddedToNav() {
        given(List.of(
                flow(CashFlowType.CALL, "-5000000", LocalDate.of(2021, 1, 1)),
                flow(CashFlowType.CALL, "-1000000", LocalDate.of(2024, 11, 1))),
                nav("5000000", LocalDate.of(2024, 9, 30)));

        assertEquals(new BigDecimal("1.00"), service.calculateMOIC(ID));
    }

    @Test
    void rolledForwardNavIsFlooredAtZero() {
        given(List.of(
                flow(CashFlowType.CALL, "-2000000", LocalDate.of(2021, 1, 15)),
                flow(CashFlowType.CALL, "-3000000", LocalDate.of(2022, 6, 1)),
                flow(CashFlowType.DISTRIBUTION, "8000000", LocalDate.of(2024, 12, 1))),
                nav("5000000", LocalDate.of(2024, 9, 30)));

        assertEquals(new BigDecimal("1.60"), service.calculateMOIC(ID));
    }

    @Test
    void entrySignDoesNotChangeResults() {
        Valuation v = nav("1210000", LocalDate.of(2023, 1, 1));
        given(List.of(flow(CashFlowType.CALL, "1000000", LocalDate.of(2021, 1, 1))), v);
        double positiveEntry = service.calculateIRR(ID);
        BigDecimal positiveMoic = service.calculateMOIC(ID);

        given(List.of(flow(CashFlowType.CALL, "-1000000", LocalDate.of(2021, 1, 1))), v);
        assertEquals(positiveEntry, service.calculateIRR(ID), 1e-9);
        assertEquals(positiveMoic, service.calculateMOIC(ID));
        assertEquals(10.0, positiveEntry, 0.05);
    }
}
