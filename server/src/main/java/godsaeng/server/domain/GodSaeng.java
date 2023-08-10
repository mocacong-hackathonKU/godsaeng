package godsaeng.server.domain;


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

    @OneToMany(mappedBy = "godSaeng", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "god_saeng_week", nullable = false)
    private List<GodSaengWeek> weeks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private GodSaengStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @OneToMany(mappedBy = "godSaeng", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<GodSaengMember> members = new ArrayList<>();

    public GodSaeng(String title, String description, List<GodSaengWeek> weeks, Member member) {
        this.title = title;
        this.description = description;
        this.weeks = weeks;
        // 같생을 만든 사람은 자동 참여
        this.members.add(new GodSaengMember(this, member));
        this.owner = member;
    }

    public GodSaeng(String title, String description, Member member) {
        this.title = title;
        this.description = description;
        this.members.add(new GodSaengMember(this, member));
        this.owner = member;
    }

    public void addAllWeek(List<Week> weeks) {
        for (Week week : weeks) {
            this.weeks.add(new GodSaengWeek(this,week));
        }
    }
}
