package likelion.prolink.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ChatResponse {
    private Long userId;
    private Long projectId;
    private String content;
    private String gptComment;
    private LocalDateTime summarizeTime;
}
