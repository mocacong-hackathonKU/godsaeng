package godsaeng.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "proof", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"god_saeng_id", "member_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proof extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_id")
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "god_saeng_id")
    private GodSaeng godSaeng;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "proof_image_id")
    private ProofImage proofImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Proof(String content, GodSaeng godSaeng, ProofImage proofImage, Member member) {
        this.content = content;
        this.godSaeng = godSaeng;
        this.proofImage = proofImage;
        this.member = member;
    }

    public static void removeMember(Proof proof) {
        proof.member = null;
    }

    public boolean isSameGodSaeng(GodSaeng godSaeng) {
        return godSaeng.getId().equals(this.godSaeng.getId());
    }
}
