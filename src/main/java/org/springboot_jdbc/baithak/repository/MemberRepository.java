package org.springboot_jdbc.baithak.repository;

import org.springboot_jdbc.baithak.model.member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;
import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<member, UUID> {
    List<member> findAllByOrderByNameAsc();
}
