package com.pjb.kindergarten_suggestion.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "school", "parent" }) })
public class SchoolRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "school")
    private School school;

    @ManyToOne
    @JoinColumn(name = "parent")
    private User parent;

    @Column
    private byte rating1;

    @Column
    private byte rating2;

    @Column
    private byte rating3;

    @Column
    private byte rating4;

    @Column
    private byte rating5;

    @Lob
    private String feedback;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public double getAvgRating() {
        return (rating1 + rating2 + rating3 + rating4 + rating5) / 5.0;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
