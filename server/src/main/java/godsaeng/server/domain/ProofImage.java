package godsaeng.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "proof_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProofImage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_image_id")
    private Long id;

    @Lob
    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "god_saeng_id")
    private GodSaeng godSaeng;

    public ProofImage(String imgUrl, GodSaeng godSaeng) {
        this.imgUrl = imgUrl;
        this.godSaeng = godSaeng;
    }

    public ProofImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
