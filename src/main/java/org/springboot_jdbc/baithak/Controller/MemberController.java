package org.springboot_jdbc.baithak.Controller;

import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<member> getAllMembers() {
        return memberRepository.findAll();
    }

    @PostMapping
    public member createMember(@RequestBody member member) {
        return memberRepository.save(member);
    }
}
