package com.pjb.kindergarten_suggestion.dto;

import com.pjb.kindergarten_suggestion.entities.Address;
import com.pjb.kindergarten_suggestion.entities.ChildAge;
import com.pjb.kindergarten_suggestion.entities.EducationMethod;
import com.pjb.kindergarten_suggestion.entities.Facility;
import com.pjb.kindergarten_suggestion.entities.ImageUrl;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolType;
import com.pjb.kindergarten_suggestion.entities.Utilities;
import com.pjb.kindergarten_suggestion.validation.Email;
import com.pjb.kindergarten_suggestion.validation.PhoneNumber;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolCreateDTO {
  private String schoolCode;

  @NotBlank(message = "School name is required")
  private String name;

  @NotNull(message = "School type is required")
  private SchoolType schoolType;

  @Email private String email;

  @PhoneNumber private String phone;

  @NotNull(message = "Address is required")
  private Address address;

  @NotNull(message = "Child age is required")
  private ChildAge childAge;

  @NotNull(message = "Education method is required")
  private EducationMethod educationMethod;

  @NotNull(message = "Fee from is required")
  @DecimalMin(value = "0", message = "Fee must be greater than or equal to 0")
  private Long feeFrom;

  @NotNull(message = "Fee to is required")
  @DecimalMin(value = "0", message = "Fee must be greater than or equal to 0")
  private Long feeTo;

  private Set<Facility> facilities;
  private Set<Utilities> utilities;
  private String description;
  private List<String> images;

  public SchoolCreateDTO(School school) {
    this.schoolCode = school.getSchoolCode();
    this.name = school.getName();
    this.schoolType = school.getSchoolType();
    this.email = school.getEmail();
    this.phone = school.getPhone();
    this.address = school.getAddress();
    this.childAge = school.getChildAge();
    this.educationMethod = school.getEducationMethod();
    this.feeFrom = school.getFeeFrom();
    this.feeTo = school.getFeeTo();
    this.facilities = school.getFacilities();
    this.utilities = school.getUtilities();
    this.description = school.getDescription();
    this.images = school.getImages().stream().map(ImageUrl::getUrl).collect(Collectors.toList());
  }
}
