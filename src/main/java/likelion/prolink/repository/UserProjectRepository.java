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

    // 사용자가 참여한 특정 프로젝트를 찾는 쿼리
    Optional<UserProject> findByUserAndProject(User user, Project project);

    List<UserProject> findByUserAndIsAcceptedTrue(User user);
    List<UserProject> findByProjectAndIsAcceptedTrue(Project project);

    Optional<UserProject> findByUserAndProjectAndIsAcceptedTrue(User user, Project project);
}
