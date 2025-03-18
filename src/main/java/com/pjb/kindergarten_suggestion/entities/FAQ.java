package com.pjb.kindergarten_suggestion.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String question;
    @Lob
    private String answer;
    @Column
    private boolean status;

    @Override
    public String toString() {
        return "FAQ{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", status=" + status +
                '}';
    }
}
