package org.springboot_jdbc.baithak.service;

import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<member> getAllMembers() {
        return memberRepository.findAll();
    }
}
