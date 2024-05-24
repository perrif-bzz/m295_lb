package com.perrif.m295_lb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Car
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Owner_ID", nullable = false)
    private Owner owner;

    @Column(name = "Make")
    @Size(min = 2, max = 45, message = "Make must be 2 to 45 letters long.")
    private String make;

    @Column(name = "Model")
    @Size(min = 2, max = 45, message = "Model must be 2 to 45 letters long.")
    private String model;

    @Column(name = "Production_Date")
    @PastOrPresent(message = "Car must have already been produced.")
    private LocalDate productionDate;

    @Column(name = "Approved")
    private Boolean approved;

    @Column(name = "Price")
    @PositiveOrZero(message = "Car price must be a positive number.")
    private BigDecimal price;
}
