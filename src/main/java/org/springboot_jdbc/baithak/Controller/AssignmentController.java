package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.dto.AssignmentDTO;
import org.springboot_jdbc.baithak.dto.ManualAssignmentRequest;
import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.AssignmentRepository;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springboot_jdbc.baithak.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springboot_jdbc.baithak.repository.PlaceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/assign")
@CrossOrigin(origins = "https://baithak-production.up.railway.app/")
public class AssignmentController {

    @Autowired
    private AssignmentService service;
    @Autowired
    private AssignmentRepository assignmentRepo;


    @PostMapping("/run")
    public ResponseEntity<String> runAssignment(@RequestParam int vaarCode, @RequestParam int week) {
        service.assignWeekly(vaarCode, week);
        return ResponseEntity.ok("Assignment completed for vaarCode=" + vaarCode);
    }
    @GetMapping("/view")
    public ResponseEntity<List<AssignmentDTO>> viewAssignments(
            @RequestParam int vaarCode,
            @RequestParam int week
    ) {
        List<Assignment> assignments = service.getAssignmentsByWeekAndVaarCode(week, vaarCode);
        if (assignments == null) {
            assignments = List.of(); // return empty list if null
        }

        List<AssignmentDTO> dtoList = assignments.stream()
                .map(a -> new AssignmentDTO(
                        a.getMember().getName(),
                        a.getPlace().getName(),
                        a.getDayOfWeek(),
                        a.getWeekNumber()
                ))
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/available-places")
    public ResponseEntity<List<places>> getAvailablePlaces(
            @RequestParam int vaarCode,
            @RequestParam int week) {
        List<places> available = service.getAvailablePlaces(vaarCode, week);
        System.out.println("Fetched " + available.size() + " available places");
        return ResponseEntity.ok(available);
    }
    @PostMapping("/manual")
    public ResponseEntity<String> manualAssign(@RequestBody ManualAssignmentRequest request) {
        service.manualAssign(request.getMemberName(), request.getPlaceName(), request.getWeek());
        return ResponseEntity.ok("Manually assigned.");
    }
    @DeleteMapping("/assignments/clear")
    public ResponseEntity<String> deleteAllAssignments() {
        assignmentRepo.deleteAll();
        return ResponseEntity.ok("All assignments deleted successfully.");
    }
    @GetMapping("/assigned-place")
    public ResponseEntity<Map<String, Object>> getAssignedPlace(
            @RequestParam UUID memberId,
            @RequestParam int vaarCode,
            @RequestParam int weekNumber) {

        places assignedPlace = assignmentRepo.findAssignedPlace(memberId, vaarCode, weekNumber);

        if (assignedPlace != null) {
            return ResponseEntity.ok(Map.of(
                    "isAssigned", true,
                    "placeName", assignedPlace.getName()
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "isAssigned", false
            ));
        }
    }




}
