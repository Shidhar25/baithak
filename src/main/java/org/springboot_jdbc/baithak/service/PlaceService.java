package org.springboot_jdbc.baithak.service;

import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public places create(places place) {
        return placeRepository.save(place);
    }

    public List<places> getAll() {
        return placeRepository.findAll();
    }

    public places update(UUID id, places newPlace) {
        return placeRepository.findById(id).map(place -> {
            place.setName(newPlace.getName());
            place.setFemaleAllowed(newPlace.getFemaleAllowed());
            place.setVaarCode(newPlace.getVaarCode());
            place.setVaarName(newPlace.getVaarName());
            place.setTimingCode(newPlace.getTimingCode());
            place.setTimeSlot(newPlace.getTimeSlot());
            return placeRepository.save(place);
        }).orElse(null);
    }

    public void delete(UUID id) {
        placeRepository.deleteById(id);
    }

    public places getById(UUID id) {
        return placeRepository.findById(id).orElse(null);
    }
}
