package com.perrif.m295_lb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Owner
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "AHV_Number")
    @Size(min = 16, max = 16, message = "AHV number must be 16 characters long.")
    private String ahvNr;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Car> cars;
}
