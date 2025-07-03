package org.springboot_jdbc.baithak.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springboot_jdbc.baithak.dto.PersonScheduleRow;
import org.springboot_jdbc.baithak.dto.WeeklyScheduleRow;
import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelExportService {

    @Autowired
    private AssignmentRepository assignmentRepo;

    // Define the desired export days (excluding शुक्रवार and शनिवार)
    private static final List<String> EXPORT_DAYS = Arrays.asList(
            "सोमवार", "मंगळवार", "बुधवार", "गुरुवार", "रविवार"
    );

    public ByteArrayInputStream generateExcelForWeek(int weekNumber) throws IOException {
        List<Assignment> assignments = assignmentRepo.findByWeekNumber(weekNumber);

        Map<String, WeeklyScheduleRow> scheduleMap = new LinkedHashMap<>();

        for (Assignment a : assignments) {
            String memberName = a.getMember().getName();
            String placeName = a.getPlace().getName();
            String day = a.getDayOfWeek();

            // Only include selected days
            if (EXPORT_DAYS.contains(day)) {
                scheduleMap.putIfAbsent(memberName, new WeeklyScheduleRow(memberName));
                scheduleMap.get(memberName).addAssignment(day, placeName);
            }
        }

        return buildExcel(scheduleMap, weekNumber);
    }

    private ByteArrayInputStream buildExcel(Map<String, WeeklyScheduleRow> scheduleMap, int weekNumber) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Week " + weekNumber);
            CellStyle bold = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            bold.setFont(font);

            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("॥ श्री राम कार्य ॥");

            Row subtitleRow = sheet.createRow(1);
            subtitleRow.createCell(0).setCellValue("॥ जय जय रघुवीर समर्थ ॥");

            Row header = sheet.createRow(2);
            header.createCell(0).setCellValue("सदस्याचे नाव");
            for (int i = 0; i < EXPORT_DAYS.size(); i++) {
                Cell cell = header.createCell(i + 1);
                cell.setCellValue(EXPORT_DAYS.get(i));
                cell.setCellStyle(bold);
            }

            int rowIdx = 3;
            for (WeeklyScheduleRow row : scheduleMap.values()) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(row.getMemberName());
                for (int i = 0; i < EXPORT_DAYS.size(); i++) {
                    String val = row.getDayPlaceMap().getOrDefault(EXPORT_DAYS.get(i), "");
                    r.createCell(i + 1).setCellValue(val);
                }
            }

            for (int i = 0; i <= EXPORT_DAYS.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generatePersonalizedExcelForWeek(int weekNumber) throws IOException {
        List<Assignment> assignments = assignmentRepo.findByWeekNumber(weekNumber);
        Map<String, PersonScheduleRow> personMap = new LinkedHashMap<>();

        for (Assignment a : assignments) {
            String name = a.getMember().getName();
            String gender = a.getMember().getGender();
            String group = gender.equalsIgnoreCase("F") ? "महिला" : "पुरुष";
            String place = a.getPlace().getName();
            String day = a.getDayOfWeek();
            String time = group.equals("महिला") ? "स. ८:४५ ते १०:३०" : "रात्री ८:४५ ते १०:३०";

            LocalDate date = a.getAssignmentDate() != null
                    ? a.getAssignmentDate()
                    : calculateDateFromWeek(weekNumber, day);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            personMap.putIfAbsent(name, new PersonScheduleRow(name));
            personMap.get(name).addEntry(formattedDate, day, group, place, time);
        }

        return buildPersonalizedExcel(personMap, weekNumber);
    }

    private LocalDate calculateDateFromWeek(int weekNumber, String marathiDay) {
        Map<String, DayOfWeek> map = Map.of(
                "सोमवार", DayOfWeek.MONDAY,
                "मंगळवार", DayOfWeek.TUESDAY,
                "बुधवार", DayOfWeek.WEDNESDAY,
                "गुरुवार", DayOfWeek.THURSDAY,
                "शुक्रवार", DayOfWeek.FRIDAY,
                "शनिवार", DayOfWeek.SATURDAY,
                "रविवार", DayOfWeek.SUNDAY
        );

        LocalDate baseDate = LocalDate.of(2025, 1, 6); // Start of week 1
        LocalDate weekStart = baseDate.plusWeeks(weekNumber - 1);
        DayOfWeek target = map.getOrDefault(marathiDay, DayOfWeek.MONDAY);

        while (weekStart.getDayOfWeek() != target) {
            weekStart = weekStart.plusDays(1);
        }

        return weekStart;
    }

    private ByteArrayInputStream buildPersonalizedExcel(Map<String, PersonScheduleRow> personMap, int weekNumber) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Personal Week " + weekNumber);

            CellStyle bold = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            bold.setFont(boldFont);

            int rowNum = 0;
            for (PersonScheduleRow person : personMap.values()) {
                sheet.createRow(rowNum++).createCell(0).setCellValue("॥ श्री राम समर्थ ॥");
                sheet.createRow(rowNum++).createCell(0).setCellValue("॥ जय जय रघुवीर समर्थ ॥");
                sheet.createRow(rowNum++).createCell(0).setCellValue("श्री सदस्याचे नाव - " + person.getMemberName());

                Row header = sheet.createRow(rowNum++);
                String[] headers = {"दिनांक", "वार", "स्त्री/पु.", "श्री बैठकिचे ठिकाण", "वेळ"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(bold);
                }

                for (PersonScheduleRow.ScheduleEntry e : person.getEntries()) {
                    Row r = sheet.createRow(rowNum++);
                    r.createCell(0).setCellValue(e.getDate());
                    r.createCell(1).setCellValue(e.getDay());
                    r.createCell(2).setCellValue(e.getGroup());
                    r.createCell(3).setCellValue(e.getPlace());
                    r.createCell(4).setCellValue(e.getTime());
                }

                rowNum++; // space after each member
            }

            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

//    all row and colums
public ByteArrayInputStream generateExcelForWeekRange(int startWeek, int endWeek) throws IOException {
    List<Assignment> allAssignments = new ArrayList<>();
    for (int i = startWeek; i <= endWeek; i++) {
        allAssignments.addAll(assignmentRepo.findByWeekNumber(i));
    }

    // Map: [day][place][memberName]
    Map<String, Map<String, Set<String>>> vaarPlaceMemberMap = new LinkedHashMap<>();

    for (Assignment a : allAssignments) {
        String vaar = a.getDayOfWeek();
        if ("शुक्रवार".equals(vaar) || "शनिवार".equals(vaar)) continue;

        String place = a.getPlace().getName();
        String name = a.getMember().getName();

        vaarPlaceMemberMap
                .computeIfAbsent(vaar, k -> new LinkedHashMap<>())
                .computeIfAbsent(place, k -> new LinkedHashSet<>())
                .add(name);
    }

    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Week " + startWeek + "-" + endWeek);

        int rowIdx = 0;
        Row title = sheet.createRow(rowIdx++);
        title.createCell(0).setCellValue("वार");
        title.createCell(1).setCellValue("ठिकाण");
        title.createCell(2).setCellValue("सदस्य");

        for (var vaarEntry : vaarPlaceMemberMap.entrySet()) {
            String vaar = vaarEntry.getKey();
            for (var placeEntry : vaarEntry.getValue().entrySet()) {
                String place = placeEntry.getKey();
                for (String member : placeEntry.getValue()) {
                    Row r = sheet.createRow(rowIdx++);
                    r.createCell(0).setCellValue(vaar);
                    r.createCell(1).setCellValue(place);
                    r.createCell(2).setCellValue(member);
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}


}
