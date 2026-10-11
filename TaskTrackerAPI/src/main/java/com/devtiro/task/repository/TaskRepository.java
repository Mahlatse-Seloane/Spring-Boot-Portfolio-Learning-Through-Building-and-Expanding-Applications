package com.devtiro.task.repository;

import com.devtiro.task.domain.entity.Task;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<@NonNull Task, @NonNull UUID>
{
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, UUID taskId);
}
