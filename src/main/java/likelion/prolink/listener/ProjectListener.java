package likelion.prolink.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import likelion.prolink.domain.Project;

import java.time.LocalDateTime;

public class ProjectListener {
    @PrePersist
    public void prePersist(Project project) {
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(Project project) {
        project.setUpdatedAt(LocalDateTime.now());
    }
}

