package org.springboot_jdbc.baithak.service;

import org.springboot_jdbc.baithak.model.member;
import org.springboot_jdbc.baithak.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<member> getAllMembers() {
        return memberRepository.findAll();

    }
    public member createmember (member member){
            return memberRepository.save(member);
        }

    public member updatemember (UUID id, member updated){
            return memberRepository.findById(id).map(existing -> {
                existing.setName(updated.getName());
                existing.setGender(updated.getGender());
                existing.setPhoneNumber(updated.getPhoneNumber());
                return memberRepository.save(existing);
            }).orElseThrow(() -> new RuntimeException("Member not found"));
        }

    public void deleteMember (UUID id){
            memberRepository.deleteById(id);
        }
    }
