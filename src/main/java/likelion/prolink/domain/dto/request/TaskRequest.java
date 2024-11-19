package likelion.prolink.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 일정 저장
@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {
    private String description;
    private String start;
    private String end;
    private Boolean attainment;
}
