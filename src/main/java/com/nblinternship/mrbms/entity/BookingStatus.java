package com.nblinternship.mrbms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BookingStatus")
public class BookingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatusID")
    private Integer statusId;

    @Column(name = "StatusName", length = 50, nullable = false, unique = true)
    private String statusName;

    public BookingStatus() {
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}