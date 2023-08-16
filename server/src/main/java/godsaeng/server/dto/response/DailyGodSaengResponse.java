package godsaeng.server.dto.response;

import godsaeng.server.domain.GodSaengStatus;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DailyGodSaengResponse {

    private long id;
    private String title;
    private GodSaengStatus stauts;
    private boolean isDone;
}
