package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;


import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelService;

    @GetMapping("/download/week/{weekNumber}")
    public ResponseEntity<Resource> downloadExcel(@PathVariable int weekNumber) throws IOException, IOException {
        ByteArrayInputStream in = excelService.generateExcelForWeek(weekNumber);
        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=week_" + weekNumber + "_assignments.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
    @GetMapping("/download/personalized/week/{weekNumber}")
    public ResponseEntity<Resource> downloadPersonalized(@PathVariable int weekNumber) {
        try {
            ByteArrayInputStream in = excelService.generatePersonalizedExcelForWeek(weekNumber);
            InputStreamResource file = new InputStreamResource(in);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=personal_week_" + weekNumber + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (Exception e) {
            e.printStackTrace(); // See terminal for error
            return ResponseEntity.internalServerError().build();
        }
    }



}
