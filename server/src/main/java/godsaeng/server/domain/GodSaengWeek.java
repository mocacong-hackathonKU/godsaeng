package godsaeng.server.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "god_saeng_week",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"god_saeng_id", "week"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GodSaengWeek extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "god_saeng_week_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "god_saeng_id")
    private GodSaeng godSaeng;


    @Enumerated(EnumType.STRING)
    @Column(name = "week")
    private Week week;

    public GodSaengWeek(Week week) {
        this.week = week;
    }

    public GodSaengWeek(GodSaeng godSaeng, Week week) {
        this.godSaeng = godSaeng;
        this.week = week;
    }
}
