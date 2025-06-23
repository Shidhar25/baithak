package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assign")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepo;

    @GetMapping("/available-places")
    public ResponseEntity<List<places>> getAvailablePlaces(
            @RequestParam int vaarCode,
            @RequestParam int week
    ) {
        List<places> availablePlaces = placeRepo.findAvailablePlacesForWeek(vaarCode, week);
        return ResponseEntity.ok(availablePlaces);
    }
}
