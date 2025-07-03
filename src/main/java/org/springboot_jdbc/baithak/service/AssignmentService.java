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
        return assignmentRepo.findByWeekAndVaarCode(week, vaarCode);
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

        // Validate gender eligibility
        if (!Boolean.TRUE.equals(p.getFemaleAllowed()) && m.getGender().equalsIgnoreCase("female")) {
            throw new IllegalArgumentException("This place is not allowed for female members.");
        }


        // Check if place is already assigned
        boolean alreadyAssigned = assignmentRepo.existsByPlaceIdAndWeekNumber(p.getId(), week);
        if (alreadyAssigned) {
            throw new IllegalArgumentException("This place is already assigned in this week.");
        }

        // Check if member already has assignment for that vaar
        boolean vaarConflict = assignmentRepo.existsAssignmentByMemberAndVaarCodeAndWeek(
                m.getId(), p.getVaarCode(), week
        );
        if (vaarConflict) {
            throw new IllegalArgumentException("This member already has an assignment on " + p.getVaarName());
        }

        // Check recent repetition
        int startWeek = Math.max(1, week - 9);
        boolean repeated = assignmentRepo.existsInVaarRangeLastWeeks(
                m.getId(), p.getId(), p.getVaarCode(), startWeek, week - 1
        );
        if (repeated) {
            throw new IllegalArgumentException("This member already had this place on same vaar in last 10 weeks.");
        }

        // Save assignment
        Assignment a = new Assignment();
        a.setId(UUID.randomUUID());
        a.setMember(m);
        a.setPlace(p);
        a.setAssignedDate(LocalDate.now());
        a.setAssignmentDate(calculateAssignmentDate(p.getVaarCode(), week));
        a.setDayOfWeek(p.getVaarName());
        a.setWeekNumber(week);
        a.setManual(true);
        a.setCreatedAt(LocalDateTime.now());

        assignmentRepo.save(a);
    }
    public void assignWithGenderLogic(int vaarCode, int week) {
        List<places> allPlaces = placeRepo.findByVaarCode(vaarCode);
        List<member> allMembers = memberRepo.findAllByOrderByNameAsc();

        // Split members
        List<member> femaleMembers = allMembers.stream()
                .filter(m -> m.getGender().equalsIgnoreCase("female"))
                .toList();

        List<member> maleMembers = allMembers.stream()
                .filter(m -> m.getGender().equalsIgnoreCase("male"))
                .toList();

        // Split places
        List<places> femaleAllowedPlaces = allPlaces.stream()
                .filter(p -> Boolean.TRUE.equals(p.getFemaleAllowed()))
                .toList();

        List<places> remainingPlaces = new ArrayList<>(allPlaces); // include all initially
        Set<UUID> assignedPlaceIds = new HashSet<>();

        // Step 1: Assign female members to female-allowed places
        int fIdx = 0;
        for (places place : femaleAllowedPlaces) {
            for (int attempts = 0; attempts < femaleMembers.size(); attempts++) {
                member f = femaleMembers.get(fIdx);

                boolean recent = assignmentRepo.existsRecentAssignment(f.getId(), place.getId(), week - 9);
                boolean alreadyThisWeek = assignmentRepo.wasAssignedThisWeek(f.getId(), week);

                if (!recent && !alreadyThisWeek) {
                    assign(place, f, week);
                    assignedPlaceIds.add(place.getId());
                    break;
                }

                fIdx = (fIdx + 1) % femaleMembers.size();
            }
        }

        // Step 2: Assign remaining unassigned places to male members
        List<places> unassignedPlaces = remainingPlaces.stream()
                .filter(p -> !assignedPlaceIds.contains(p.getId()))
                .toList();

        int mIdx = 0;
        for (places place : unassignedPlaces) {
            for (int attempts = 0; attempts < maleMembers.size(); attempts++) {
                member m = maleMembers.get(mIdx);

                boolean recent = assignmentRepo.existsRecentAssignment(m.getId(), place.getId(), week - 9);
                boolean alreadyThisWeek = assignmentRepo.wasAssignedThisWeek(m.getId(), week);

                if (!recent && !alreadyThisWeek) {
                    assign(place, m, week);
                    assignedPlaceIds.add(place.getId());
                    break;
                }

                mIdx = (mIdx + 1) % maleMembers.size();
            }
        }
    }

    private void assign(places place, member member, int week) {
        Assignment a = new Assignment();
        a.setId(UUID.randomUUID());
        a.setPlace(place);
        a.setMember(member);
        a.setWeekNumber(week);
        a.setDayOfWeek(place.getVaarName());
        a.setAssignedDate(LocalDate.now());
        a.setAssignmentDate(calculateAssignmentDate(place.getVaarCode(), week));
        a.setManual(false);
        a.setCreatedAt(LocalDateTime.now());

        assignmentRepo.save(a);
    }

    public LocalDate calculateAssignmentDate(int vaarCode, int weekNumber) {
        LocalDate today = LocalDate.now();

        // Find the Sunday of the current week
        LocalDate sunday = today.minusDays(today.getDayOfWeek().getValue() % 7);

        // Add (weekNumber - 1) weeks and (vaarCode - 1) days to get the final assignment date
        return sunday.plusWeeks(weekNumber - 1).plusDays(vaarCode - 1);
    }









}
