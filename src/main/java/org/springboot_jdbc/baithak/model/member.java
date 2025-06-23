package org.springboot_jdbc.baithak.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
public class member {
    @jakarta.persistence.Id
    @Id
    private UUID id;
    private String name;
    private String gender;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;

    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;

    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}

