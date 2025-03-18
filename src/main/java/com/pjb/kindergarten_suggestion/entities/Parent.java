package com.pjb.kindergarten_suggestion.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String fullName;

    @Column(length = 100)
    private String studentInformation;

    @Column(nullable = false)
    private LocalDateTime dob;

    @Column(length = 10, unique = true)
    private String phoneNumber;
}