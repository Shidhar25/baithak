package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    @Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE a.member.id = :memberId AND a.place.id = :placeId AND a.weekNumber >= :cutoffWeek")
    boolean existsRecentAssignment(@Param("memberId") UUID memberId, @Param("placeId") UUID placeId, @Param("cutoffWeek") int cutoffWeek);

    @Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE a.member.id = :memberId AND a.weekNumber = :week")
    boolean wasAssignedThisWeek(@Param("memberId") UUID memberId, @Param("week") int week);

    @Query("""
    SELECT a FROM Assignment a 
    WHERE a.weekNumber = :week 
      AND a.place.vaarCode = :vaarCode
""")
    List<Assignment> findByWeekAndVaarCode(@Param("week") int week, @Param("vaarCode") int vaarCode);


}