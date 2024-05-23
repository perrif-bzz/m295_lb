package com.perrif.m295_lb.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "todo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Todo
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "todoValue")
    @Length(min = 2, max = 20, message = "Todo value must be between 2 and 20 letters long.")
    private String todoValue;
}
