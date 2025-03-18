package com.pjb.kindergarten_suggestion.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String fullName;

    @Column(length = 100)
    private String studentInformation;

    @Column(nullable = false)
    private LocalDateTime dob;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}