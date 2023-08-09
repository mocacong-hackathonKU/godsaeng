package godsaeng.server.controller;

import godsaeng.server.dto.request.AppleLoginRequest;
import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.request.KakaoLoginRequest;
import godsaeng.server.dto.response.OAuthTokenResponse;
import godsaeng.server.dto.response.TokenResponse;
import godsaeng.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Login", description = "인증")
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "자체 로그인")
    @PostMapping
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid AuthLoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카카오 OAuth 로그인")
    @PostMapping("/kakao")
    public ResponseEntity<OAuthTokenResponse> loginKakao(@RequestBody @Valid KakaoLoginRequest request) {
        OAuthTokenResponse response = authService.kakaoOAuthLogin(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "애플 OAuth 로그인")
    @PostMapping("/apple")
    public ResponseEntity<OAuthTokenResponse> loginApple(@RequestBody @Valid AppleLoginRequest request) {
        OAuthTokenResponse response = authService.appleOAuthLogin(request);
        return ResponseEntity.ok(response);
    }
}
