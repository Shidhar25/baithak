package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springboot_jdbc.baithak.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "https://baithak-production.up.railway.app/")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;
    @GetMapping
    public ResponseEntity<List<member>> getAllMembers() {
        return ResponseEntity.ok(memberRepository.findAll());
    }
        @PostMapping("/add")
        public ResponseEntity<member> create(@RequestBody member member) {
            return ResponseEntity.ok(memberService.createmember(member));
        }

        // PUT
        @PutMapping("/{id}")
        public ResponseEntity<member> update(@PathVariable UUID id, @RequestBody member member) {
            return ResponseEntity.ok(memberService.updatemember(id, member));
        }

        // DELETE
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable UUID id) {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build();
        }

        // Optional: GET all members
        @GetMapping("/all")
        public ResponseEntity<List<member>> getAll() {
            return ResponseEntity.ok(memberService.getAllMembers());
        }
    }
