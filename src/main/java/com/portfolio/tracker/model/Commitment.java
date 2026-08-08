package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;


@Entity
@Data
@NoArgsConstructor

public class Commitment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal commitmentAmount;
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false) 
    private Fund fund;

    @OneToMany(mappedBy = "commitment", cascade = CascadeType.ALL)
    private List<CashFlow> cashFlows;
}