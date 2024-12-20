package likelion.prolink.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String description;
    private LocalDate start;
    private LocalDate end;
    private Boolean attainment;

    public Task(User user, Project project, String description, LocalDate start, LocalDate end, Boolean attainment) {
        this.user = user;
        this.project = project;
        this.description = description;
        this.start = start;
        this.end = end;
        this.attainment = attainment;
    }
}

