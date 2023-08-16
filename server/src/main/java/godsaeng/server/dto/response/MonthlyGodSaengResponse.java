package godsaeng.server.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class MonthlyGodSaengResponse {

    private LocalDate day;
    private Boolean isDone;
}
