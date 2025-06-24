package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<places, UUID> {
    List<places> findByVaarCode(int vaarCode);

    @Query(value = "SELECT * FROM places p WHERE p.vaar_code = :vaarCode AND p.id NOT IN (" +
            "SELECT place_id FROM assignments WHERE week_number = :week)", nativeQuery = true)
    List<places> findAvailablePlacesForWeek(@Param("vaarCode") int vaarCode, @Param("week") int week);
    places findByName(String name);
    @Query("""
    SELECT p FROM female_places p
    WHERE p.vaarCode = :vaarCode
      AND p.femaleAllowed = :femaleAllowed
      AND p.id NOT IN (
        SELECT a.place.id FROM Assignment a
        WHERE a.weekNumber = :week
      )
""")
    List<places> findAvailablePlacesForWeekAndGender(@Param("vaarCode") int vaarCode,
                                                     @Param("week") int week,
                                                     @Param("femaleAllowed") boolean femaleAllowed);


}