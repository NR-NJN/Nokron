package com.portfolio.tracker.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Valuation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;
    private BigDecimal navAmount;

    
    @ManyToOne
    @JoinColumn(name = "comm_id", nullable = false) 
    private Commitment commitment;
    
}
