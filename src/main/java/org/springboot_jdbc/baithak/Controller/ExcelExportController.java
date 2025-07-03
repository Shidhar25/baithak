package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;


import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "https://baithak-sigma.vercel.app/")
//@CrossOrigin(origins = "http://localhost:3000")
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
    @GetMapping("/matrix")
    public ResponseEntity<?> downloadMatrix(
            @RequestParam int start,
            @RequestParam int end) {
        try {
            ByteArrayInputStream stream = excelService.generateExcelMatrixWithVaar(start, end);
            InputStreamResource resource = new InputStreamResource(stream);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=matrix.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to generate excel: " + e.getMessage());
        }
    }
    @GetMapping("/history")
    public ResponseEntity<InputStreamResource> downloadMemberHistory(
            @RequestParam String memberName) throws IOException {
        ByteArrayInputStream in = excelService.generateMemberHistory(memberName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + memberName + "_history.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }





}
