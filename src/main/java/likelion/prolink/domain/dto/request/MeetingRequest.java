package likelion.prolink.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MeetingRequest {
    private String title;
    private String content;
}
