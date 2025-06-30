package org.springboot_jdbc.baithak.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "places")
public class places {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(name = "is_female_allowed")
    private Boolean isFemaleAllowed = false;
    @Column(name = "vaar_code")
    private Integer vaarCode;

    @Column(name = "vaar_name")
    private String vaarName;

    @Column(name = "timing_code")
    private Integer timingCode;

    @Column(name = "time_slot")
    private String timeSlot;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFemaleAllowed() {
        return isFemaleAllowed;
    }

    public void setFemaleAllowed(Boolean femaleAllowed) {
        isFemaleAllowed = femaleAllowed;
    }

    public Integer getVaarCode() {
        return vaarCode;
    }

    public void setVaarCode(Integer vaarCode) {
        this.vaarCode = vaarCode;
    }

    public String getVaarName() {
        return vaarName;
    }

    public void setVaarName(String vaarName) {
        this.vaarName = vaarName;
    }

    public Integer getTimingCode() {
        return timingCode;
    }

    public void setTimingCode(Integer timingCode) {
        this.timingCode = timingCode;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}