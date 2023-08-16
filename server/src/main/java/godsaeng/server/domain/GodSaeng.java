package godsaeng.server.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "god_saeng")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GodSaeng extends BaseTime {

    private static final int DOING_WEEKS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "god_saeng_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "godSaeng", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GodSaengWeek> weeks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @OneToMany(mappedBy = "godSaeng", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<GodSaengMember> members = new ArrayList<>();

    public GodSaeng(String title, String description, List<Week> weeks, Member member) {
        this.title = title;
        this.description = description;
        List<GodSaengWeek> godSaengWeeks = weeks.stream()
                .map(week -> new GodSaengWeek(this, week)).collect(Collectors.toList());
        this.weeks.addAll(godSaengWeeks);
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

    public GodSaengStatus getStatus() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(getClosedDate())) {
            return GodSaengStatus.DONE;
        }
        if (now.isBefore(getOpenedDate())) {
            return GodSaengStatus.BEFORE;
        }
        return GodSaengStatus.PROCEEDING;
    }

    public LocalDate getOpenedDate() {
        LocalDate baseTime = super.getCreatedTime().toLocalDateTime().toLocalDate();
        return baseTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    public LocalDate getClosedDate() {
        LocalDate date = getOpenedDate();

        for (int i = 0; i < DOING_WEEKS; i++) {
            date = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }

        return date.minusDays(1);
    }

    public List<LocalDate> getDoingDate() {
        List<LocalDate> doingDates = new ArrayList<>();
        LocalDate openedDate = getOpenedDate().minusDays(1);
        for (int i = 0; i < DOING_WEEKS; i++) {
            for (GodSaengWeek week : weeks) {
                LocalDate nextDate = openedDate.with(TemporalAdjusters.next(week.toDayOfWeek()));
                doingDates.add(nextDate);
            }
            openedDate = openedDate.plusWeeks(1);
        }
        return doingDates;
    }
}
