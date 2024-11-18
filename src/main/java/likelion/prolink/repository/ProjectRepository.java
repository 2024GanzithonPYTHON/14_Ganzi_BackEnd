package likelion.prolink.repository;

import likelion.prolink.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByIsRecruit(Boolean isRecruit);
    List<Project> findAllByIsRecruitAndCategory(Boolean isRecruit, String category);
}
