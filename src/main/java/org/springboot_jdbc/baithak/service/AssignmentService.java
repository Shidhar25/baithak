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

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired private MemberRepository memberRepo;
    @Autowired private PlaceRepository placeRepo;
    @Autowired
    private AssignmentRepository assignmentRepo;
    @Autowired private RotationStateRepository rotationRepo;

    public void assignWeekly(int vaarCode, int week) {
        List<member> members = memberRepo.findAllByOrderByNameAsc();
        List<places> places = placeRepo.findByVaarCode(vaarCode);
        RotationState state = rotationRepo.getLastState();
        int lastIndex = (state != null) ? state.getLastUsedMemberIndex() : 0;

        int memberPointer = lastIndex;
        int memberCount = members.size();

        for (places place : places) {
            int attempts = 0;
            while (attempts < memberCount) {
                Member m = (Member) members.get(memberPointer);
                boolean skip = assignmentRepo.existsRecentAssignment(((member) m).getId(), place.getId(), week - 9)
                        || assignmentRepo.wasAssignedThisWeek(((member) m).getId(), week)
                        || !((member) m).getGender().equals("male");

                if (!skip) {
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
                    break;
                }

                memberPointer = (memberPointer + 1) % memberCount;
                attempts++;
            }
        }

        RotationState newState = new RotationState();
        newState.setLastUsedMemberIndex(memberPointer);
        newState.setUpdatedAt(LocalDateTime.now());
        rotationRepo.save(newState);
    }
}
