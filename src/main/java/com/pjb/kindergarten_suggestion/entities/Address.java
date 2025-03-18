package com.pjb.kindergarten_suggestion.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "wards_id")
    private Wards wards;

    @Column(length = 100)
    private String detail;

    public String toString() {
        String provinceName = (province != null) ? province.getName() : "";
        String districtName = (district != null) ? " - " + district.getName() : "";
        String wardsName = (wards != null) ? " - " + wards.getName() : "";
        return provinceName + districtName + wardsName;
    }

    public Address(Province province, District district, Wards wards) {
        this.province = province;
        this.district = district;
        this.wards = wards;
    }
}
