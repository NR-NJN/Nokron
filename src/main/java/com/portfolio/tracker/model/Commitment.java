package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import java.math.BigDecimal;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class Commitment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal commitmentAmount;
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    @ToString.Exclude
    private Fund fund;

    @OneToMany(mappedBy = "commitment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CashFlow> cashFlows;
}
