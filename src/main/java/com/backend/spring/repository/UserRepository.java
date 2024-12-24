package com.backend.spring.repository;

import java.util.List;
import java.util.Optional;

import com.backend.spring.entity.ERole;
import com.backend.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByRoles_Name(ERole eRole);

    long countByRoles_Name(ERole eRole);

    boolean existsByPhoneNumber(String phoneNumber);

    User findByVerificationCode(String verificationCode);

    User findByEmail(String to);
}
