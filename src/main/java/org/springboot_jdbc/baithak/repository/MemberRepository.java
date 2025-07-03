package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<member, UUID> {
    List<member> findAllByOrderByNameAsc();
    member findByName(String name);
    Optional<member> findByNameIgnoreCase(String name);
    List<member> findByGenderIgnoreCase(String gender);
    member findByNameAndGender(String name, String gender);

    List<member> findByGenderIgnoreCaseOrderByNameAsc(String gender);



}
