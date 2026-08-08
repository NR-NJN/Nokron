package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
public class CashFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    private BigDecimal amount; 
    
    private String type;

    @ManyToOne
    @JoinColumn(name = "com_id", nullable = false)
    private Commitment commitment;
}
