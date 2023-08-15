package godsaeng.server.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GodSaengTest {

    @Test
    void getOpenedDate() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        weeks.add(Week.FRI);


        String email1 = "rlawjddn103@naver.com";

        GodSaeng godSaeng = new GodSaeng(title, description, weeks, new Member(email1, Platform.KAKAO, "11111"));
        System.out.println("godSaeng.getOpenedDate() = " + godSaeng.getOpenedDate());
        System.out.println("godSaeng.getClosedDate() = " + godSaeng.getClosedDate());
        System.out.println("godSaeng.getDoingDate() = " + godSaeng.getDoingDate());

    }

    @Test
    void getStatus() {
        String title = "아침 6시 반 기상 갓생 살기";
        String description = "아침 6시 반 기상 후 유의미한 일을 하고 인증해야합니다.";
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        weeks.add(Week.FRI);


        String email1 = "rlawjddn103@naver.com";

        GodSaeng godSaeng = new GodSaeng(title, description, weeks, new Member(email1, Platform.KAKAO, "11111"));
        System.out.println("godSaeng.getOpenedDate() = " + godSaeng.getStatus());
    }
}