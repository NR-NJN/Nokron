package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class Fund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String assetClass;
    
    @OneToMany(mappedBy = "fund", cascade = CascadeType.ALL) //root of my problems remember
    private List<Commitment> commitments;
}
