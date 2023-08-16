package godsaeng.server.acceptance;

import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.response.TokenResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AcceptanceFixtures {

    public static ExtractableResponse<Response> 회원_가입(MemberSignUpRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    public static String 로그인_토큰_발급(String email, String password) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new AuthLoginRequest(email, password))
                .when().post("/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TokenResponse.class)
                .getToken();
    }

    public static ExtractableResponse<Response> 회원정보_조회(String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .when().get("/members/mypage")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
