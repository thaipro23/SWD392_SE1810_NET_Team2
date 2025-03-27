package com.pjb.kindergarten_suggestion.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "child") // Đảm bảo tên bảng đúng
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 20) // Xác định tên cột chính xác
    private String fullName;

    @Column(name = "student_information", length = 100) // Xác định tên cột chính xác
    private String studentInformation;

    @Column(name = "dob", nullable = false) // Xác định tên cột chính xác
    private LocalDateTime dob;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false) // Đảm bảo tên khóa ngoại đúng
    private User parent;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false) // Đảm bảo tên khóa ngoại đúng
    private User teacher;
}
