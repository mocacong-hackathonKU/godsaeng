package godsaeng.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum Week {

    MON(1),
    TUE(2),
    WED(3),
    THR(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private int value;

    public DayOfWeek toDayOfWeek() {
        return DayOfWeek.of(value);
    }
}
