package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<member>> getAllMembers() {
        return ResponseEntity.ok(memberRepository.findAll());
    }
}
