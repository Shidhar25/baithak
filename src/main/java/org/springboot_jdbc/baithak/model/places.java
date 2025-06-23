package org.springboot_jdbc.baithak.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "places")
public class places {
    @jakarta.persistence.Id
    @Id
    private UUID id;
    private String name;
    private Boolean isFemaleAllowed;
    private Integer vaarCode;
    private String vaarName;
    private Integer timingCode;
    private String timeSlot;
    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}