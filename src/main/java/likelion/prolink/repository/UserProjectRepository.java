package likelion.prolink.repository;

import likelion.prolink.domain.Project;
import likelion.prolink.domain.User;
import likelion.prolink.domain.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    Long countByProjectAndIsAcceptedTrue(Project project);

    List<UserProject> findByProject(Project project);

    Optional<UserProject> findByUser(User user);
}
