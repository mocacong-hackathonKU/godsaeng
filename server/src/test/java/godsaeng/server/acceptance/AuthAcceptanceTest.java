package godsaeng.server.acceptance;

import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.response.TokenResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("회원이 정상적으로 로그인한다")
    void login() {
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                "메리");
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .extract();
        AuthLoginRequest loginRequest = new AuthLoginRequest("dlawotn3@naver.com", "a1b2c3d4");

        TokenResponse tokenResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TokenResponse.class);

        assertNotNull(tokenResponse);
        assertNotNull(tokenResponse.getToken());
    }
}
