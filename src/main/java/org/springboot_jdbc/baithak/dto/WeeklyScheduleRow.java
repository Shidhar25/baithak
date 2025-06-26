package org.springboot_jdbc.baithak.dto;

import java.util.HashMap;
import java.util.Map;

public class WeeklyScheduleRow {
    private String memberName;
    private Map<String, String> dayPlaceMap = new HashMap<>();

    public WeeklyScheduleRow(String memberName) {
        this.memberName = memberName;
    }

    public void addAssignment(String day, String place) {
        dayPlaceMap.put(day, place);
    }

    public String getMemberName() {
        return memberName;
    }

    public Map<String, String> getDayPlaceMap() {
        return dayPlaceMap;
    }

    public void setDayPlaceMap(Map<String, String> dayPlaceMap) {
        this.dayPlaceMap = dayPlaceMap;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
// Getters

}
