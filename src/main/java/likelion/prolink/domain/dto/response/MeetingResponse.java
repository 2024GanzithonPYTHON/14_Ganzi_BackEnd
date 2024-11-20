package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MeetingResponse {
    private Long id;
    private Long userId;
    private Long projectId;
    private String title;
    private String content;
    private String gptComment;
    private LocalDate meetingDate;

    public MeetingResponse(Long id, Long userId, Long projectId, String title, String content, String gptComment, LocalDate meetingDate) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.title = title;
        this.content = content;
        this.gptComment = gptComment;
        this.meetingDate = meetingDate;
    }
}
