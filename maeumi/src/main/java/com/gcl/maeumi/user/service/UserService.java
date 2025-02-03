package com.gcl.maeumi.user.service;

import com.gcl.maeumi.user.dto.UserDto.SignupRequestDto;
import com.gcl.maeumi.user.entity.User;
import com.gcl.maeumi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    /*
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }*/

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> join(SignupRequestDto signupRequestDto) {
        // username 중복 체크
        Optional<User> originUser = userRepository.findByUsername(signupRequestDto.getUsername());
        if (originUser.isPresent()) {
            return Optional.empty();
        }

        // 비밀번호 암호화
        //String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // 회원 생성 및 저장
        User newUser = User.builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword())
                .name(signupRequestDto.getName())
                .build();

        // 회원 정보 저장
        userRepository.save(newUser);

        return Optional.of(newUser);
    }
}
