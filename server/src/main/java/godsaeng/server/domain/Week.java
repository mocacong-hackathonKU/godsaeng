package godsaeng.server.domain;

import godsaeng.server.exception.badrequest.InvalidPlatformException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum Week {

    MON("MON"),
    TUE("TUE"),
    WED("WED"),
    THR("THR"),
    FRI("FRI"),
    SAT("SAT"),
    SUN("SUN");

    private String value;

    public static Week from(String value) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.value, value))
                .findFirst()
                .orElseThrow(InvalidPlatformException::new);
    }
}
