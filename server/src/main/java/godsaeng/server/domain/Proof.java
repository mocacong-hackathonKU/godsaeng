package godsaeng.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "proof")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proof extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "god_saeng_id")
    private GodSaeng godSaeng;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "proof_image_id")
    private ProofImage proofImage;

    public Proof(String description, GodSaeng godSaeng, ProofImage proofImage) {
        this.description = description;
        this.godSaeng = godSaeng;
        this.proofImage = proofImage;
    }
}
