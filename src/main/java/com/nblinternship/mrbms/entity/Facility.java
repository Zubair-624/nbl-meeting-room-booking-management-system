package com.nblinternship.mrbms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FacilityID")
    private Integer facilityId;

    @Column(name = "FacilityName", length = 100, nullable = false, unique = true)
    private String facilityName;

    public Facility() {
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return facilityId != null && facilityId.equals(facility.facilityId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}