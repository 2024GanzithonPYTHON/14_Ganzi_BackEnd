package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TaskResponse {
    private Long taskId;
    private Long userId;
    private Long projectId;
    private String description;
    private String start;
    private String end;
    private Boolean attainment;

    public TaskResponse(Long taskId, Long userId, Long projectId, String description, String start, String end, Boolean attainment) {
        this.taskId = taskId;
        this.userId = userId;
        this.projectId = projectId;
        this.description = description;
        this.start = start;
        this.end = end;
        this.attainment = attainment;
    }
}
