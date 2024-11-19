package likelion.prolink.repository;

import likelion.prolink.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByProjectId(Long ProjectId);
    Optional<Meeting> findByIdAndProjectId(Long meetingId, Long projectId);
}
