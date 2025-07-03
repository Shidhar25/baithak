package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.dto.AssignmentDTO;
import org.springboot_jdbc.baithak.dto.ManualAssignmentRequest;
import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.AssignmentRepository;
import org.springboot_jdbc.baithak.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/assign")
@CrossOrigin(origins = "https://baithak-sigma.vercel.app/")
public class AssignmentController {

    @Autowired
    private AssignmentService service;

    @Autowired
    private AssignmentRepository assignmentRepo;

    // 🟢 Run auto-assignment
    @PostMapping("/run")
    public ResponseEntity<String> runAssignment(
            @RequestParam int vaarCode,
            @RequestParam int week
    ) {
        service.assignWithGenderLogic(vaarCode, week);
        return ResponseEntity.ok("✅ Assignment done for vaarCode=" + vaarCode + ", week=" + week);
    }

    // 🟢 View assignments
    @GetMapping("/view")
    public ResponseEntity<List<AssignmentDTO>> viewAssignments(
            @RequestParam int vaarCode,
            @RequestParam int week
    ) {
        List<Assignment> assignments = service.getAssignmentsByWeekAndVaarCode(week, vaarCode);
        if (assignments == null) assignments = List.of();

        List<AssignmentDTO> dtoList = assignments.stream()
                .map(a -> new AssignmentDTO(
                        a.getMember().getName(),
                        a.getPlace().getName(),
                        a.getDayOfWeek(),
                        a.getWeekNumber()
                )).toList();

        return ResponseEntity.ok(dtoList);
    }

    // 🟢 Get available places
    @GetMapping("/available-places")
    public ResponseEntity<List<places>> getAvailablePlaces(
            @RequestParam int vaarCode,
            @RequestParam int week) {
        List<places> available = service.getAvailablePlaces(vaarCode, week);
        System.out.println("Fetched " + available.size() + " available places");
        return ResponseEntity.ok(available);
    }

    // 🟢 Manual assign with 10-week repetition confirmation
    @PostMapping("/manual")
    public ResponseEntity<Map<String, Object>> manualAssign(@RequestBody ManualAssignmentRequest request) {
        try {
            boolean repeated = service.manualAssign(
                    request.getMemberName(),
                    request.getPlaceName(),
                    request.getWeek()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "✅ Assigned");
            response.put("repeated", repeated);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "❌ Failed",
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "status", "❌ Internal error",
                    "error", e.getMessage()
            ));
        }
    }


    // 🟢 Delete all assignments
    @DeleteMapping("/assignments/clear")
    public ResponseEntity<String> deleteAllAssignments() {
        assignmentRepo.deleteAll();
        return ResponseEntity.ok("✅ All assignments deleted successfully.");
    }

    // 🟢 Get assigned place for a member
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
