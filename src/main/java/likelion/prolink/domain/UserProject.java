package likelion.prolink.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @NoArgsConstructor
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String authority;
    private String careerUrl;
    private String content;
    private Boolean isAccepted;

    public UserProject(User user, Project project, String authority, String careerUrl, String content, Boolean isAccepted) {
        this.user = user;
        this.project = project;
        this.authority = authority;
        this.careerUrl = careerUrl;
        this.content = content;
        this.isAccepted = isAccepted;
    }
}
