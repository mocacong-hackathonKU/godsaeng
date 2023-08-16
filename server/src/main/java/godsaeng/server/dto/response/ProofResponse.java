package godsaeng.server.dto.response;

import godsaeng.server.domain.Proof;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ProofResponse {
    private String name;
    private String profileImg;
    private String proofImg;
    private String proofDetail;

    public static ProofResponse from(Proof proof) {
        return new ProofResponse(
                proof.getMember().getNickname(),
                proof.getMember().getImgUrl(),
                proof.getProofImage().getImgUrl(),
                proof.getContent()
        );
    }
}
