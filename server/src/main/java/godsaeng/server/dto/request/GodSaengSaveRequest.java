package godsaeng.server.dto.request;

import godsaeng.server.domain.Week;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class GodSaengSaveRequest {
    @NotBlank(message = "2001:공백일 수 없습니다.")
    private String title;
    @NotBlank(message = "2001:공백일 수 없습니다.")
    private String description;
    @NotBlank(message = "2001:공백일 수 없습니다.")
    private List<Week> weeks;
}
