package godsaeng.server.acceptance;

import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.response.MyPageResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static godsaeng.server.acceptance.AcceptanceFixtures.로그인_토큰_발급;
import static godsaeng.server.acceptance.AcceptanceFixtures.회원_가입;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("회원을 정상적으로 가입한다")
    void signUp() {
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com",
                "a1b2c3d4", "메리");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("회원이 정상적으로 탈퇴한다")
    void delete() {
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        회원_가입(request);
        String token = 로그인_토큰_발급(request.getEmail(), request.getPassword());

        RestAssured.given().log().all()
                .auth().oauth2(token)
                .when().delete("/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("존재하지 않는 닉네임은 닉네임 중복검사에서 걸리지 않는다")
    void isDuplicateWithNonExistingNickname() {
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("value", request.getNickname())
                .when().get("/members/check-duplicate/nickname")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("이미 존재하는 닉네임은 닉네임 중복검사에서 걸린다")
    void isDuplicateWithExistingNickname() {
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("value", request.getNickname())
                .when().get("/members/check-duplicate/nickname")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("마이페이지로 내 정보를 조회한다")
    void findMyInfo() {
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        회원_가입(signUpRequest);
        String token = 로그인_토큰_발급(signUpRequest.getEmail(), signUpRequest.getPassword());

        MyPageResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .when().get("/members/mypage")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MyPageResponse.class);

        assertAll(
                () -> assertThat(actual.getEmail()).isEqualTo("dlawotn3@naver.com"),
                () -> assertThat(actual.getNickname()).isEqualTo("메리"),
                () -> assertThat(actual.getImgUrl()).isNull()
        );
    }
}
