package org.springboot_jdbc.baithak.model;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "assignments")
public class Assignment {
    @jakarta.persistence.Id
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private member member;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private places place;

    private LocalDate assignedDate;
    private String dayOfWeek;
    private Integer weekNumber;
    private Boolean isManual;
    private LocalDateTime createdAt;
    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public places getPlace() {
        return place;
    }

    public void setPlace(places place) {
        this.place = place;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
    public void setMember(member member) {
        this.member = (member) member;
    }
    public member getMember() {
        return (member) member;
    }
}
