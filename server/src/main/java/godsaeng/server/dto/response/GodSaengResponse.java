package godsaeng.server.dto.response;

import godsaeng.server.domain.Week;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class GodSaengResponse {
    private Long id;
    private String title;
    private String description;
    private List<Week> week;
}
