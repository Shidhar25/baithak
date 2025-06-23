package org.springboot_jdbc.baithak.service;

import org.springboot_jdbc.baithak.model.Assignment;
import org.springboot_jdbc.baithak.model.RotationState;
import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.model.places;
import org.springboot_jdbc.baithak.repository.AssignmentRepository;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springboot_jdbc.baithak.repository.PlaceRepository;
import org.springboot_jdbc.baithak.repository.RotationStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private PlaceRepository placeRepo;
    @Autowired
    private AssignmentRepository assignmentRepo;
    @Autowired
    private RotationStateRepository rotationRepo;

    public void assignWeekly(int vaarCode, int week) {
        List<member> members = memberRepo.findAllByOrderByNameAsc();
        List<places> places = placeRepo.findByVaarCode(vaarCode);

        RotationState state = rotationRepo.getLastState();
        int lastIndex = (state != null) ? state.getLastUsedMemberIndex() : 0;

        int memberPointer = lastIndex;
        int memberCount = members.size();

        for (places place : places) {
            int attempts = 0;
            boolean assigned = false;

            while (attempts < memberCount) {
                member m = members.get(memberPointer);

                // Check if member is eligible
                boolean recentlyAssigned = assignmentRepo.existsRecentAssignment(m.getId(), place.getId(), week - 9);
                boolean alreadyAssignedThisWeek = assignmentRepo.wasAssignedThisWeek(m.getId(), week);
                boolean genderMismatch = !m.getGender().equalsIgnoreCase("male") && !place.getFemaleAllowed();

                if (!recentlyAssigned && !alreadyAssignedThisWeek && !genderMismatch) {
                    // Create assignment
                    Assignment a = new Assignment();
                    a.setId(UUID.randomUUID());
                    a.setMember(m);
                    a.setPlace(place);
                    a.setAssignedDate(LocalDate.now());
                    a.setDayOfWeek(place.getVaarName());
                    a.setWeekNumber(week);
                    a.setManual(false);
                    a.setCreatedAt(LocalDateTime.now());
                    assignmentRepo.save(a);

                    assigned = true;
                    break;
                }

                memberPointer = (memberPointer + 1) % memberCount;
                attempts++;
            }

            // if not assigned, you can log skipped place
            if (!assigned) {
                System.out.println("Skipped place: " + place.getVaarName() + " (No suitable member found)");
            }
        }

        // Save updated state
        RotationState newState = new RotationState();
        newState.setLastUsedMemberIndex(memberPointer);
        newState.setUpdatedAt(LocalDateTime.now());
        rotationRepo.save(newState);
    }
    public void assignManual(UUID memberId, int vaarCode, int week) {
        // Find available places of the vaarCode
        List<places> availablePlaces = placeRepo.findAvailablePlacesForWeek(vaarCode, week);
        if (availablePlaces.isEmpty()) throw new RuntimeException("No available places for vaarCode: " + vaarCode);

        // Choose first available place (or show dropdown in frontend)
        places selectedPlace = availablePlaces.get(0); // frontend should choose

        // Save assignment
        Assignment a = new Assignment();
        a.setId(UUID.randomUUID());
        a.setMember(memberRepo.findById(memberId).orElseThrow());
        a.setPlace(selectedPlace);
        a.setAssignedDate(LocalDate.now());
        a.setDayOfWeek(selectedPlace.getVaarName());
        a.setWeekNumber(week);
        a.setManual(true); // manual assignment
        a.setCreatedAt(LocalDateTime.now());

        assignmentRepo.save(a);
    }

    public List<Assignment> getAssignmentsByWeekAndVaarCode(int week, int vaarCode) {
        return assignmentRepo.findByWeekAndVaarCode(week, vaarCode);
    }
    public List<places> getAvailablePlaces(int vaarCode, int week) {
        List<places> result = placeRepo.findAvailablePlacesForWeek(vaarCode, week);
        System.out.println("Available places for vaarCode=" + vaarCode + ", week=" + week + ": " + result.size());
        return result;
    }



}
