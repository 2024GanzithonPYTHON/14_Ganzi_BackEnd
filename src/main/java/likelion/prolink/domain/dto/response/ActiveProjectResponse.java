package likelion.prolink.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveProjectResponse {
    private Long projectId;
    private String nickName;
    private String projectName;
    private String title;
    private Boolean isActive;
    private String content;
}
