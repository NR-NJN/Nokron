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
public class Valuation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;
    private BigDecimal navAmount;


    @ManyToOne
    @JoinColumn(name = "comm_id", nullable = false)
    @ToString.Exclude
    private Commitment commitment;

}
