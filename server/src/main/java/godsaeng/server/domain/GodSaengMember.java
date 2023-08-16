package godsaeng.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "god_saeng_member",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"god_saeng_id", "member_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GodSaengMember extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "god_saeng_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "god_saeng_id")
    private GodSaeng godSaeng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public GodSaengMember(GodSaeng godSaeng, Member member) {
        this.godSaeng = godSaeng;
        this.member = member;
    }

    public static void removeMember(GodSaengMember godSaengMember) {
        godSaengMember.member = null;
    }
}
