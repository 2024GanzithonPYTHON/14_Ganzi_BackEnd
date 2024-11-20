package likelion.prolink.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// 일정 저장
@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {
    private String description;
    private LocalDate start;
    private LocalDate end;
}
