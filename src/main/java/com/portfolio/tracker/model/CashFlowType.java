package com.portfolio.tracker.model;

import java.math.BigDecimal;

public enum CashFlowType {
    CALL(true),
    DISTRIBUTION(false);

    private final boolean contribution;

    CashFlowType(boolean contribution) {
        this.contribution = contribution;
    }

    public boolean isContribution() {
        return contribution;
    }

    // Investor's point of view: money paid into the fund is negative, money received is positive,
    // regardless of the sign the amount was entered with.
    public BigDecimal signed(BigDecimal amount) {
        return contribution ? amount.abs().negate() : amount.abs();
    }
}
