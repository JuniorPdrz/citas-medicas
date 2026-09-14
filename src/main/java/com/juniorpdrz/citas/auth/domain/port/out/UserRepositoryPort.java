package com.juniorpdrz.citas.auth.domain.port.out;

import com.juniorpdrz.citas.auth.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
}