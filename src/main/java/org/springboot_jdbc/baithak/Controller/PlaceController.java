package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.dto.ManualAssignmentRequest;
import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import org.springboot_jdbc.baithak.service.AssignmentService;
import org.springboot_jdbc.baithak.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://baithak-production.up.railway.app/")
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping("/add")
    public places createPlace(@RequestBody places place) {
        return placeService.create(place);
    }

    @GetMapping("/All")
    public List<places> getAllPlaces() {
        return placeService.getAll();
    }

    @GetMapping("/{id}")
    public places getPlaceById(@PathVariable UUID id) {
        return placeService.getById(id);
    }

    @PutMapping("/{id}")
    public places updatePlace(@PathVariable UUID id, @RequestBody places place) {
        return placeService.update(id, place);
    }

    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable UUID id) {
        placeService.delete(id);
    }


}
