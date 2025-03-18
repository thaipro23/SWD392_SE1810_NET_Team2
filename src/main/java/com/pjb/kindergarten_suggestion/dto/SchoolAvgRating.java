package com.pjb.kindergarten_suggestion.dto;

import java.util.List;
import lombok.Data;

@Data
public class SchoolAvgRating {
  private List<Byte> rating;

  public SchoolAvgRating(Byte rating1, Byte rating2, Byte rating3, Byte rating4, Byte rating5) {
    this.rating = List.of(rating1, rating2, rating3, rating4, rating5);
  }
}
