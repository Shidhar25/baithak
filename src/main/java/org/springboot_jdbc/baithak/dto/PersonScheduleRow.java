package org.springboot_jdbc.baithak.dto;

import java.util.ArrayList;
import java.util.List;

public class PersonScheduleRow {
    private final String memberName;
    private final List<ScheduleEntry> entries = new ArrayList<>();

    public PersonScheduleRow(String memberName) {
        this.memberName = memberName;
    }

    public void addEntry(String date, String day, String group, String place, String time) {
        entries.add(new ScheduleEntry(date, day, group, place, time));
    }

    public String getMemberName() {
        return memberName;
    }

    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    public static class ScheduleEntry {
        private final String date, day, group, place, time;

        public ScheduleEntry(String date, String day, String group, String place, String time) {
            this.date = date;
            this.day = day;
            this.group = group;
            this.place = place;
            this.time = time;
        }

        public String getDate() { return date; }
        public String getDay() { return day; }
        public String getGroup() { return group; }
        public String getPlace() { return place; }
        public String getTime() { return time; }
    }
}
