package godsaeng.server.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ProofSaveRequest {

    @NotBlank(message = "2001:공백일 수 없습니다.")
    private String proofContent;
}
