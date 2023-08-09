package godsaeng.server.domain;


import godsaeng.server.exception.badrequest.DuplicateWeekException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "god_saeng")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GodSaeng extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "god_saeng_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
    private String description;

    @ElementCollection
    @CollectionTable(name = "god_saeng_week", joinColumns = @JoinColumn(name = "god_saeng_id"))
    @Column(name = "god_saeng_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Week> weeks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GodSaengStatus status;

    @OneToMany(mappedBy = "godSaeng", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GodSaengMember> members = new ArrayList<>();

    public GodSaeng(String title, String description, List<Week> weeks) {
        this.title = title;
        this.description = description;
        this.weeks = weeks;
        validateWeeks();
    }

    private void validateWeeks() {
        long count = weeks.stream().distinct().count();
        if (count != weeks.size()) {
            throw new DuplicateWeekException();
        }
    }
}
