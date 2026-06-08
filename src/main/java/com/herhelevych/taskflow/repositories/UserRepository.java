package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.User;
import com.herhelevych.taskflow.domain.GlobalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.username = :username")
    Optional<User> findByUsernameWithProjects(String username);
    boolean existsByUsername(String username);
    long countByRole(GlobalRole role);

    Optional<User> findByUsername(String username);
}
