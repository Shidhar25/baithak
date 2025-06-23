package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.dto.AssignmentDTO;
import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import java.util.List;

@RestController
@RequestMapping("/api/assign")
public class AssignmentController {

//    @Autowired
//    private AssignmentService service;
//
//    @PostMapping("/run")
//    public ResponseEntity<String> runAssignment(@RequestParam int vaarCode, @RequestParam int week) {
//        service.assignWeekly(vaarCode, week);
//        return ResponseEntity.ok("Assignment completed for vaarCode=" + vaarCode);
//    }
//    @GetMapping("/view")
//    public ResponseEntity<List<AssignmentDTO>> viewAssignments(
//            @RequestParam int vaarCode,
//            @RequestParam int week
//    ) {
//        List<Assignment> assignments = service.getAssignmentsByWeekAndVaarCode(week, vaarCode);
//
//        List<AssignmentDTO> dtoList = assignments.stream()
//                .map(a -> new AssignmentDTO(
//                        a.getMember().getName(),
//                        a.getPlace().getName(),
//                        a.getDayOfWeek(),
//                        a.getWeekNumber()
//                ))
//                .toList();
//
//        return ResponseEntity.ok(dtoList);
//    }
//    @GetMapping("/available-places")
//    public ResponseEntity<List<places>> getAvailablePlaces(
//            @RequestParam int vaarCode,
//            @RequestParam int week) {
//        List<places> available = service.getAvailablePlaces(vaarCode, week);
//        System.out.println("Fetched " + available.size() + " available places");
//        return ResponseEntity.ok(available);
//    }





}
