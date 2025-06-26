package org.springboot_jdbc.baithak.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rotation_state")
public class RotationState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gender;
    private Integer lastUsedMemberIndex;
    private LocalDateTime updatedAt;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLastUsedMemberIndex(Integer lastUsedMemberIndex) {
        this.lastUsedMemberIndex = lastUsedMemberIndex;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getLastUsedMemberIndex() {
        return lastUsedMemberIndex;
    }

    public void setLastUsedMemberIndex(int memberPointer) {
        this.lastUsedMemberIndex = memberPointer;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.updatedAt = now;
    }
}
