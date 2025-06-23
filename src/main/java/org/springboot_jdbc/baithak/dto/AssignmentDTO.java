package org.springboot_jdbc.baithak.dto;


import java.util.UUID;

public class AssignmentDTO {
    private String memberName;
    private String placeName;
    private String dayOfWeek;
    private int weekNumber;

    public AssignmentDTO(String memberName, String placeName, String dayOfWeek, int weekNumber) {
        this.memberName = memberName;
        this.placeName = placeName;
        this.dayOfWeek = dayOfWeek;
        this.weekNumber = weekNumber;
    }

    // Getters and Setters
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }
    public record PlaceDTO(UUID id, String name, String timeSlot) {}

}
