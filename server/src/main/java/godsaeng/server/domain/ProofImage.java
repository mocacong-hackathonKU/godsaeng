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

    public ProofImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
