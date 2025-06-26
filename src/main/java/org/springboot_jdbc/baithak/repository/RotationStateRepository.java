package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.RotationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RotationStateRepository extends JpaRepository<RotationState, Long> {
    @Query("SELECT r FROM RotationState r ORDER BY r.updatedAt DESC LIMIT 1")
    RotationState getLastState();

    @Query("SELECT r FROM RotationState r WHERE r.gender = :gender ORDER BY r.updatedAt DESC LIMIT 1")
    RotationState getLastStateForGender(String gender);

}
