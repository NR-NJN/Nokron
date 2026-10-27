package com.portfolio.tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Fund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String assetClass;

    @OneToMany(mappedBy = "fund", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Commitment> commitments;
}
