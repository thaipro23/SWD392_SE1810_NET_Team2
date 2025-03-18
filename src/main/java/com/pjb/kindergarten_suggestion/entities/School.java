package com.pjb.kindergarten_suggestion.entities;

import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.dto.SchoolCreateDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class School {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String schoolCode;

  @Column private String name;

  @ManyToOne
  @JoinColumn(name = "school_type_id")
  private SchoolType schoolType;

  @Column private String email;

  @Column(length = 20)
  private String phone;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id")
  private Address address;

  @ManyToOne
  @JoinColumn(name = "child_age_id")
  private ChildAge childAge;

  @ManyToOne
  @JoinColumn(name = "education_method_id")
  private EducationMethod educationMethod;

  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long feeFrom;

  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long feeTo;

  @ManyToMany
  @JoinTable(
      name = "school_facility",
      joinColumns = @JoinColumn(name = "school_id"),
      inverseJoinColumns = @JoinColumn(name = "facility_id"))
  private Set<Facility> facilities;

  @ManyToMany
  @JoinTable(
      name = "school_utilities",
      joinColumns = @JoinColumn(name = "school_id"),
      inverseJoinColumns = @JoinColumn(name = "utilities_id"))
  private Set<Utilities> utilities;

  @Lob private String description;

  @Enumerated(EnumType.STRING)
  private SchoolStatus status;

  @ManyToOne
  @JoinColumn(name = "creator")
  private User creator;

  @ManyToOne
  @JoinColumn(name = "acceptor")
  private User acceptor;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "school_id")
  private List<ImageUrl> images;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column private LocalDateTime updatedAt;

  @Column private LocalDateTime publishedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

  @Column(nullable = false, columnDefinition = "bigint default 0")
  @Builder.Default
  private Long totalRating = 0L;

  @Column(nullable = false, columnDefinition = "bigint default 0")
  @Builder.Default
  private Long totalRatingCount = 0L;

  public double getRating() {
    return totalRatingCount == 0 ? 0 : (double) totalRating / totalRatingCount / 5;
  }

  public School(SchoolCreateDTO schoolCreateDTO) {
    this.schoolCode = schoolCreateDTO.getSchoolCode();
    this.name = schoolCreateDTO.getName();
    this.schoolType = schoolCreateDTO.getSchoolType();
    this.email = schoolCreateDTO.getEmail();
    this.phone = schoolCreateDTO.getPhone();
    this.address = schoolCreateDTO.getAddress();
    this.childAge = schoolCreateDTO.getChildAge();
    this.educationMethod = schoolCreateDTO.getEducationMethod();
    this.feeFrom = schoolCreateDTO.getFeeFrom();
    this.feeTo = schoolCreateDTO.getFeeTo();
    this.facilities = schoolCreateDTO.getFacilities();
    this.utilities = schoolCreateDTO.getUtilities();
    this.description = schoolCreateDTO.getDescription();
    this.images =
        schoolCreateDTO.getImages().stream().map(ImageUrl::new).collect(Collectors.toList());
    this.totalRating = 0L;
    this.totalRatingCount = 0L;
  }

  public void update(SchoolCreateDTO schoolCreateDTO) {
    this.schoolCode = schoolCreateDTO.getSchoolCode();
    this.name = schoolCreateDTO.getName();
    this.schoolType = schoolCreateDTO.getSchoolType();
    this.email = schoolCreateDTO.getEmail();
    this.phone = schoolCreateDTO.getPhone();
    this.address = schoolCreateDTO.getAddress();
    this.childAge = schoolCreateDTO.getChildAge();
    this.educationMethod = schoolCreateDTO.getEducationMethod();
    this.feeFrom = schoolCreateDTO.getFeeFrom();
    this.feeTo = schoolCreateDTO.getFeeTo();
    this.facilities = schoolCreateDTO.getFacilities();
    this.utilities = schoolCreateDTO.getUtilities();
    this.description = schoolCreateDTO.getDescription();
    this.images =
        schoolCreateDTO.getImages().stream().map(ImageUrl::new).collect(Collectors.toList());
  }
}
