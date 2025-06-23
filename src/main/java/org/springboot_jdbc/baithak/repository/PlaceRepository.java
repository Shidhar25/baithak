package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<places, UUID> {
    List<places> findByVaarCode(int vaarCode);
}