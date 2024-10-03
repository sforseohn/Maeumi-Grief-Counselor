package com.gcl.maeumi.counsel.repository;

import com.gcl.maeumi.counsel.entity.Counsel;
import com.gcl.maeumi.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
    Optional<Counsel> findBySessionId(String sessionId);
}
