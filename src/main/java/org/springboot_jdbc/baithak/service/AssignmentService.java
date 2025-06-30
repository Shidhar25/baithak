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
import java.util.*;

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
        return Optional.ofNullable(assignmentRepo.findByWeekAndVaarCode(week, vaarCode))
                .orElse(List.of());
    }
    public List<places> getAvailablePlaces(int vaarCode, int week) {
        List<places> result = placeRepo.findAvailablePlacesForWeek(vaarCode, week);
        System.out.println("Available places for vaarCode=" + vaarCode + ", week=" + week + ": " + result.size());
        return result;
    }
    public void manualAssign(String memberName, String placeName, int week) {
        member m = memberRepo.findByName(memberName);
        places p = placeRepo.findByName(placeName);

        if (m == null || p == null) {
            throw new IllegalArgumentException("Invalid member or place name");
        }

        boolean alreadyAssigned = assignmentRepo.existsByPlaceIdAndWeekNumber(p.getId(), week);
        if (alreadyAssigned) {
            throw new IllegalArgumentException("This place is already assigned in this week.");
        }

        int startWeek = Math.max(1, week - 9);
        boolean repeated = assignmentRepo.existsInVaarRangeLastWeeks(
                m.getId(), p.getId(), p.getVaarCode(), startWeek, week - 1
        );

        if (repeated) {
            throw new IllegalArgumentException("This member had this place in the same vaarCode in the last 10 weeks.");
        }

        Assignment assignment = new Assignment();
        assignment.setId(UUID.randomUUID());
        assignment.setMember(m);
        assignment.setPlace(p);
        assignment.setWeekNumber(week);
        assignment.setDayOfWeek(p.getVaarName());
        assignment.setAssignedDate(LocalDate.now());
        assignment.setManual(true);
        assignment.setCreatedAt(LocalDateTime.now());

        assignmentRepo.save(assignment);
    }
    public void assignWithFemalePriority(int vaarCode, int week) {
        List<member> allMembers = memberRepo.findAllByOrderByNameAsc();
        List<member> femaleMembers = allMembers.stream()
                .filter(m -> m.getGender().equalsIgnoreCase("female"))
                .toList();
        List<member> maleMembers = allMembers.stream()
                .filter(m -> m.getGender().equalsIgnoreCase("male"))
                .toList();

        List<places> allPlaces = placeRepo.findByVaarCode(vaarCode);
        List<places> femaleAllowedPlaces = allPlaces.stream()
                .filter(places::getFemaleAllowed)
                .toList();

        Set<UUID> assignedPlaces = new HashSet<>();
        Random random = new Random();

        // Assign 2–3 places to each female
        for (member female : femaleMembers) {
            int count = 2 + random.nextInt(2); // 2 or 3
            int assigned = 0;
            for (places place : femaleAllowedPlaces) {
                if (assignedPlaces.contains(place.getId())) continue;

                boolean recent = assignmentRepo.existsRecentAssignment(female.getId(), place.getId(), week - 9);
                boolean alreadyThisWeek = assignmentRepo.wasAssignedThisWeek(female.getId(), week);

                if (!recent && !alreadyThisWeek) {
                    Assignment a = new Assignment();
                    a.setId(UUID.randomUUID());
                    a.setMember(female);
                    a.setPlace(place);
                    a.setAssignedDate(LocalDate.now());
                    a.setDayOfWeek(place.getVaarName());
                    a.setWeekNumber(week);
                    a.setManual(false);
                    a.setCreatedAt(LocalDateTime.now());
                    assignmentRepo.save(a);

                    assignedPlaces.add(place.getId());
                    if (++assigned >= count) break;
                }
            }
        }

        // Assign remaining places to male members
        List<places> remaining = allPlaces.stream()
                .filter(p -> !assignedPlaces.contains(p.getId()))
                .toList();

        assignRemainingToMales(maleMembers, remaining, vaarCode, week);
    }
    private void assignRemainingToMales(List<member> maleMembers, List<places> places, int vaarCode, int week) {
        RotationState state = rotationRepo.getLastStateForGender("male");
        int pointer = (state != null) ? state.getLastUsedMemberIndex() : 0;
        int total = maleMembers.size();

        for (places p : places) {
            int attempts = 0;
            boolean assigned = false;

            while (attempts < total) {
                member m = maleMembers.get(pointer);
                boolean recent = assignmentRepo.existsRecentAssignment(m.getId(), p.getId(), week - 9);
                boolean already = assignmentRepo.wasAssignedThisWeek(m.getId(), week);

                if (!recent && !already) {
                    Assignment a = new Assignment();
                    a.setId(UUID.randomUUID());
                    a.setMember(m);
                    a.setPlace(p);
                    a.setAssignedDate(LocalDate.now());
                    a.setDayOfWeek(p.getVaarName());
                    a.setWeekNumber(week);
                    a.setManual(false);
                    a.setCreatedAt(LocalDateTime.now());
                    assignmentRepo.save(a);

                    assigned = true;
                    break;
                }

                pointer = (pointer + 1) % total;
                attempts++;
            }

            if (!assigned) System.out.println("Skipped place: " + p.getName());
        }

        RotationState newState = new RotationState();
        newState.setGender("male");
        newState.setLastUsedMemberIndex(pointer);
        newState.setUpdatedAt(LocalDateTime.now());
        rotationRepo.save(newState);
    }
    public void assignFemaleManually(String memberName, String placeName, String dayOfWeek, int weekNumber) {
        member m = memberRepo.findByName(memberName);
        places p = placeRepo.findByName(placeName);

        if (m == null || p == null) {
            throw new IllegalArgumentException("Invalid member or place name");
        }

        if (!p.getVaarName().equalsIgnoreCase(dayOfWeek)) {
            throw new IllegalArgumentException("Day mismatch: Place is not assigned to given vaar");
        }

        boolean alreadyAssigned = assignmentRepo.existsByPlaceIdAndWeekNumber(p.getId(), weekNumber);
        if (alreadyAssigned) {
            throw new IllegalArgumentException("This place is already assigned in this week.");
        }

        boolean recent = assignmentRepo.existsRecentAssignment(m.getId(), p.getId(), weekNumber - 9);
        if (recent) {
            throw new IllegalArgumentException("This member had this place in last 10 weeks.");
        }

        Assignment a = new Assignment();
        a.setId(UUID.randomUUID());
        a.setMember(m);
        a.setPlace(p);
        a.setAssignedDate(LocalDate.now());
        a.setDayOfWeek(dayOfWeek);
        a.setWeekNumber(weekNumber);
        a.setManual(true);
        a.setCreatedAt(LocalDateTime.now());

        assignmentRepo.save(a);
    }
    public LocalDate calculateAssignmentDate(int vaarCode, int weekNumber) {
        LocalDate today = LocalDate.now();
        LocalDate sunday = today.minusDays(today.getDayOfWeek().getValue() % 7);
        return sunday.plusWeeks(weekNumber - 1).plusDays(vaarCode - 1);
    }








}
