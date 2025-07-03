package org.springboot_jdbc.baithak.dto;

import jakarta.persistence.Column;

public class ManualAssignmentRequest {
    private String memberName;
    private String placeName;
    private int week;
    @Column(name = "force_assigned")
    private boolean confirmIfRepeated = false;

    public boolean isConfirmIfRepeated() {
        return confirmIfRepeated;
    }

    public void setConfirmIfRepeated(boolean confirmIfRepeated) {
        this.confirmIfRepeated = confirmIfRepeated;
    }

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

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
