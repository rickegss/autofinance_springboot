package com.rickegss.autofinance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @Column(columnDefinition = "bytea")
    private byte[] avatar;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_categories", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "category_name")
    @Builder.Default
    private List<String> preferredCategories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Goal> goals = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private FinancialGoal financialGoal;

    @Column(precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(nullable = false)
    @Builder.Default
    private String theme = "light";

    @Column(name = "financial_month_day")
    @Builder.Default
    private Integer financialMonthDay = 1;
}