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
    public ByteArrayInputStream generateExcelMatrixWithVaar(int startWeek, int endWeek) throws IOException {
        List<Assignment> allAssignments = new ArrayList<>();
        for (int i = startWeek; i <= endWeek; i++) {
            allAssignments.addAll(assignmentRepo.findByWeekNumber(i));
        }

        Set<String> members = new TreeSet<>();               // Columns
        Set<String> places = new LinkedHashSet<>();          // Rows

        // Map: [place] -> [member] -> date
        Map<String, Map<String, String>> matrix = new LinkedHashMap<>();

        // Map: place -> vaar name & vaar code
        Map<String, String> placeVaarNameMap = new HashMap<>();
        Map<String, Integer> placeVaarCodeMap = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM");

        for (Assignment a : allAssignments) {
            String member = a.getMember().getName();
            String place = a.getPlace().getName();
            String date = a.getAssignmentDate() != null
                    ? a.getAssignmentDate().format(formatter)
                    : "";
            String vaarName = a.getPlace().getVaarName();
            int vaarCode = a.getPlace().getVaarCode();

            members.add(member);
            places.add(place);

            matrix
                    .computeIfAbsent(place, k -> new HashMap<>())
                    .put(member, date);

            placeVaarNameMap.put(place, vaarName);
            placeVaarCodeMap.put(place, vaarCode);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Week " + startWeek + "-" + endWeek);

            CellStyle[] vaarStyles = new CellStyle[8]; // vaarCode is usually 1–7
            for (int i = 1; i <= 7; i++) {
                CellStyle style = workbook.createCellStyle();
                style.setFillForegroundColor((short) (40 + i * 3));  // some arbitrary different colors
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                vaarStyles[i] = style;
            }

            // Header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ठिकाण");
            headerRow.createCell(1).setCellValue("वार");  // vaar name

            int colIdx = 2;
            for (String member : members) {
                headerRow.createCell(colIdx++).setCellValue(member);
            }

            int rowIdx = 1;
            for (String place : places) {
                Row row = sheet.createRow(rowIdx++);

                String vaarName = placeVaarNameMap.getOrDefault(place, "");
                int vaarCode = placeVaarCodeMap.getOrDefault(place, 0);

                Cell placeCell = row.createCell(0);
                placeCell.setCellValue(place);

                Cell vaarCell = row.createCell(1);
                vaarCell.setCellValue(vaarName);

                if (vaarCode >= 1 && vaarCode <= 7) {
                    placeCell.setCellStyle(vaarStyles[vaarCode]);
                    vaarCell.setCellStyle(vaarStyles[vaarCode]);
                }

                Map<String, String> assignmentsForPlace = matrix.getOrDefault(place, Collections.emptyMap());
                colIdx = 2;

                for (String member : members) {
                    String date = assignmentsForPlace.getOrDefault(member, "");
                    Cell cell = row.createCell(colIdx++);
                    cell.setCellValue(date);

                    if (vaarCode >= 1 && vaarCode <= 7) {
                        cell.setCellStyle(vaarStyles[vaarCode]);
                    }
                }
            }

            for (int i = 0; i <= members.size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


//    HIstory of every person

    public ByteArrayInputStream generateMemberHistory(String memberName) throws IOException {
        List<Assignment> history = assignmentRepo.findByMemberNameOrderByWeekNumberAsc(memberName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("History");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Week");
            header.createCell(1).setCellValue("Vaar");
            header.createCell(2).setCellValue("Place");
            header.createCell(3).setCellValue("Assignment Date");

            int rowIdx = 1;
            for (Assignment a : history) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getWeekNumber());
                row.createCell(1).setCellValue(a.getDayOfWeek());
                row.createCell(2).setCellValue(a.getPlace().getName());
                row.createCell(3).setCellValue(a.getAssignmentDate().toString());
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


}
