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
public class GodSaengAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("갓생을 등록한다")
    void saveGodSaeng() {
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
