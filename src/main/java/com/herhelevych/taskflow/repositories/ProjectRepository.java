package com.herhelevych.taskflow.repositories;

import com.herhelevych.taskflow.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    long countByIsArchivedTrue();
}
