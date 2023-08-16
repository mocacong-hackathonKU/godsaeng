package godsaeng.server.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class ProofSaveRequest {

    @NotBlank(message = "2001:공백일 수 없습니다.")
    private String content;
}
