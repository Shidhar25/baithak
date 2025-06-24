package org.springboot_jdbc.baithak.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springboot_jdbc.baithak.dto.WeeklyScheduleRow;
import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    @Autowired
    private AssignmentRepository assignmentRepo;

    public ByteArrayInputStream generateExcelForWeek(int weekNumber) throws IOException {
        List<Assignment> assignments = assignmentRepo.findByWeekNumber(weekNumber);

        Map<String, WeeklyScheduleRow> scheduleMap = new LinkedHashMap<>();

        for (Assignment a : assignments) {
            String memberName = a.getMember().getName();
            String placeName = a.getPlace().getName();
            String day = a.getDayOfWeek();

            scheduleMap.putIfAbsent(memberName, new WeeklyScheduleRow(memberName));
            scheduleMap.get(memberName).addAssignment(day, placeName);
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
            String[] days = {"सोमवार", "मंगळवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार", "रविवार"};
            for (int i = 0; i < days.length; i++) {
                header.createCell(i + 1).setCellValue(days[i]);
            }

            int rowIdx = 3;
            for (WeeklyScheduleRow row : scheduleMap.values()) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(row.getMemberName());
                for (int i = 0; i < days.length; i++) {
                    String val = row.getDayPlaceMap().getOrDefault(days[i], "");
                    r.createCell(i + 1).setCellValue(val);
                }
            }

            for (int i = 0; i < 8; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
