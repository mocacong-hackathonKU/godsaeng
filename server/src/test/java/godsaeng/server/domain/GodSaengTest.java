package godsaeng.server.domain;

import godsaeng.server.exception.badrequest.DuplicateWeekException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GodSaengTest {

    @Test
    void validateDuplicateWeek() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        Member member = new Member();

        assertThrows(DuplicateWeekException.class, () -> new GodSaeng(title, description, weeks, member));
    }

}