package likelion.prolink.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class MemberManageResponse {
    private List<UserProjectResponse> userProjectResponses;
    private String projectName;
    private String title;
    private String constructor;

    public MemberManageResponse(List<UserProjectResponse> userProjectResponses, String projectName, String title, String constructor) {
        this.userProjectResponses = userProjectResponses;
        this.projectName = projectName;
        this.title = title;
        this.constructor = constructor;
    }
}
