package com.gcl.maeumi.common;

import com.gcl.maeumi.common.error.ErrorCode;
import com.gcl.maeumi.common.error.BusinessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public class EntityFinder {
    public static <T, ID> T findByIdOrThrow(JpaRepository<T, ID> repository, ID id, ErrorCode errorCode) {
        return repository.findById(id).orElseThrow(() -> new BusinessException(errorCode));
    }

    public static <T> T findByQueryOrThrow(Optional<T> entity, ErrorCode errorCode) {
        return entity.orElseThrow(() -> new BusinessException(errorCode));
    }
}

