package com.canvi.hama.domain.user.repository;

import com.canvi.hama.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
