package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assign")
public class AssignmentController {

    @Autowired
    private AssignmentService service;

    @PostMapping("/run")
    public ResponseEntity<String> runAssignment(@RequestParam int vaarCode, @RequestParam int week) {
        service.assignWeekly(vaarCode, week);
        return ResponseEntity.ok("Assignment completed for vaarCode=" + vaarCode);
    }
}

