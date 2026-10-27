package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashFlowType type;

    @ManyToOne
    @JoinColumn(name = "com_id", nullable = false)
    @ToString.Exclude
    private Commitment commitment;

    public BigDecimal getSignedAmount() {
        return type.signed(amount);
    }
}
