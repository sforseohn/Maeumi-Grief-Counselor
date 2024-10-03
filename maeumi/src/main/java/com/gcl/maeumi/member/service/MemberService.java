package com.gcl.maeumi.member.service;

import com.gcl.maeumi.member.dto.MemberDto.SignupRequestDto;
import com.gcl.maeumi.member.entity.Member;
import com.gcl.maeumi.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<Member> join(SignupRequestDto signupRequestDto) {
        // username 중복 체크
        Optional<Member> originMember = memberRepository.findByUsername(signupRequestDto.getUsername());
        if (originMember.isPresent()) {
            return Optional.empty();
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // 회원 생성 및 저장
        Member newMember = Member.builder()
                .username(signupRequestDto.getUsername())
                .password(encodedPassword)
                .name(signupRequestDto.getName())
                .build();

        // 회원 정보 저장
        memberRepository.save(newMember);

        return Optional.of(newMember);
    }
}
