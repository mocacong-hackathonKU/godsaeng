package godsaeng.server.acceptance;

import godsaeng.server.domain.Week;
import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static godsaeng.server.acceptance.AcceptanceFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GodSaengAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("같생을 등록한다")
    void saveGodSaeng() {
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        회원_가입(signUpRequest);
        String token = 로그인_토큰_발급(signUpRequest.getEmail(), signUpRequest.getPassword());
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        GodSaengSaveRequest request = new GodSaengSaveRequest("미라클 모닝", "우리 함께 7시 이전에 일어나요!",
                weeks);

        GodSaengSaveResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .body(request)
                .when().post("/godsaengs")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GodSaengSaveResponse.class);

        assertThat(actual.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("모든 같생을 조회한다")
    void findAllGodSaeng() {
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        회원_가입(signUpRequest);
        String token = 로그인_토큰_발급(signUpRequest.getEmail(), signUpRequest.getPassword());
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        GodSaengSaveRequest saveRequest1 = new GodSaengSaveRequest("미라클 모닝", "우리 함께 7시 이전에 일어나요!",
                weeks);
        GodSaengSaveRequest saveRequest2 = new GodSaengSaveRequest("영어 공부", "우리 함께 영어 공부해염",
                weeks);
        같생_등록(token, saveRequest1);
        같생_등록(token, saveRequest2);

        GodSaengsResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .when().get("/godsaengs")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GodSaengsResponse.class);

        assertThat(actual.getGodsaengs().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("같생에 참가 신청한다")
    void attendGodSaeng() {
        Long godSaengId = 1L;
        MemberSignUpRequest signUpRequest1 = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        MemberSignUpRequest signUpRequest2 = new MemberSignUpRequest("dlawotn2@naver.com", "a1b2c3d4",
                "메이");
        회원_가입(signUpRequest1);
        회원_가입(signUpRequest2);
        String token1 = 로그인_토큰_발급(signUpRequest1.getEmail(), signUpRequest1.getPassword());
        String token2 = 로그인_토큰_발급(signUpRequest2.getEmail(), signUpRequest2.getPassword());
        List<Week> weeks = new ArrayList<>();
        weeks.add(Week.MON);
        weeks.add(Week.WED);
        GodSaengSaveRequest request = new GodSaengSaveRequest("미라클 모닝", "우리 함께 7시 이전에 일어나요!",
                weeks);
        같생_등록(token1, request);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token2)
                .when().post("/godsaengs/attend/" + godSaengId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
