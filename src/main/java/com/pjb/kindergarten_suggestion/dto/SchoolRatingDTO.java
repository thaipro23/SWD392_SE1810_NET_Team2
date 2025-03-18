package com.pjb.kindergarten_suggestion.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SchoolRatingDTO {
  private String parentName;
  private LocalDateTime createdAt;
  private String feedback;
  private int[] ratings;

  public SchoolRatingDTO(
      String parentName,
      LocalDateTime createdAt,
      String feedback,
      byte rating1,
      byte rating2,
      byte rating3,
      byte rating4,
      byte rating5) {
    this.parentName = parentName;
    this.createdAt = createdAt;
    this.feedback = feedback;
    this.ratings = new int[] {rating1, rating2, rating3, rating4, rating5};
  }

  public double getAvgRating() {
    double sum = 0;
    for (int rating : ratings) {
      sum += rating;
    }
    return sum / ratings.length;
  }
}
