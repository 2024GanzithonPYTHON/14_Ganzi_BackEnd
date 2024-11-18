package likelion.prolink.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserProjectResponse {
    private String nickName;
    private Long projectId;
    private String authority;
    private String careerUrl;
    private String content;
    private Boolean isAccepted;

    public UserProjectResponse(String nickName, Long projectId, String authority, String careerUrl, String content, Boolean isAccepted) {
        this.nickName = nickName;
        this.projectId = projectId;
        this.authority = authority;
        this.careerUrl = careerUrl;
        this.content = content;
        this.isAccepted = isAccepted;
    }
}
