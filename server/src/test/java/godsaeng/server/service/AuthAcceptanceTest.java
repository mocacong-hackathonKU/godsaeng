package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.AppleLoginRequest;
import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.request.KakaoLoginRequest;
import godsaeng.server.dto.response.OAuthTokenResponse;
import godsaeng.server.dto.response.TokenResponse;
import godsaeng.server.exception.badrequest.PasswordMismatchException;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.security.auth.OAuthPlatformMemberResponse;
import godsaeng.server.security.auth.apple.AppleOAuthUserProvider;
import godsaeng.server.security.auth.kakao.KakaoOAuthUserProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthAcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private KakaoOAuthUserProvider kakaoOAuthUserProvider;
    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 로그인 요청이 옳다면 토큰을 발급한다")
    void login() {
        String email = "dlawotn3@naver.com";
        String password = "a1b2c3d4";
        String encodedPassword = passwordEncoder.encode("a1b2c3d4");
        Member member = new Member("dlawotn3@naver.com", encodedPassword, "메리");
        memberRepository.save(member);
        AuthLoginRequest loginRequest = new AuthLoginRequest(email, password);

        TokenResponse tokenResponse = authService.login(loginRequest);

        assertNotNull(tokenResponse.getToken());
    }

    @Test
    @DisplayName("회원 로그인 요청이 올바르지 않다면 예외가 발생한다")
    void loginWithException() {
        String email = "dlawotn3@naver.com";
        String password = "a1b2c3d4";
        String encodedPassword = passwordEncoder.encode(password);
        Member member = new Member(email, encodedPassword, "메리");
        memberRepository.save(member);

        AuthLoginRequest loginRequest = new AuthLoginRequest(email, "wrongPassword");

        assertThrows(PasswordMismatchException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Kakao OAuth 로그인 시 가입되지 않은 회원일 경우 이메일 값을 보내고 isRegistered 값을 false로 보낸다")
    void loginKakaoOAuthNotRegistered() {
        String expected = "kth@kakao.com";
        String platformId = "1234321";
        when(kakaoOAuthUserProvider.getKakaoPlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.kakaoOAuthLogin(new KakaoLoginRequest("token"));

        assertAll(
                () -> assertThat(actual.getToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isFalse(),
                () -> assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }

    @Test
    @DisplayName("Kakao OAuth 로그인 시 이미 가입된 회원일 경우 토큰과 이메일, 그리고 isRegistered 값을 true로 보낸다")
    void loginKakaoOAuthRegisteredAndMocacongMember() {
        String expected = "kth@kakao.com";
        String platformId = "1234321";
        Member member = new Member(
                expected,
                null,
                "케이",
                null,
                Platform.KAKAO,
                platformId
        );
        memberRepository.save(member);
        when(kakaoOAuthUserProvider.getKakaoPlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.kakaoOAuthLogin(new KakaoLoginRequest("token"));

        assertAll(
                () -> assertThat(actual.getToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isTrue(),
                () -> assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }

    @Test
    @DisplayName("Kakao OAuth 로그인 시 등록은 완료됐지만 회원가입 절차에 실패해 닉네임이 없으면 isRegistered 값을 false로 보낸다")
    void loginKakaoOAuthRegisteredButNotMocacongMember() {
        String expected = "kth@kakao.com";
        String platformId = "1234321";
        Member member = new Member("kth@kakao.com", Platform.KAKAO, "1234321");
        memberRepository.save(member);
        when(kakaoOAuthUserProvider.getKakaoPlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.kakaoOAuthLogin(new KakaoLoginRequest("token"));

        assertAll(
                () -> assertThat(actual.getToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isFalse(),
                () -> assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }

    @Test
    @DisplayName("Apple OAuth 로그인 시 가입되지 않은 회원일 경우 이메일 값을 보내고 isRegistered 값을 false로 보낸다")
    void loginAppleOAuthNotRegistered() {
        String expected = "kth@apple.com";
        String platformId = "1234321";
        when(appleOAuthUserProvider.getApplePlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.appleOAuthLogin(new AppleLoginRequest("token"));

        assertAll(
                () -> Assertions.assertThat(actual.getToken()).isNotNull(),
                () -> Assertions.assertThat(actual.getEmail()).isEqualTo(expected),
                () -> Assertions.assertThat(actual.getIsRegistered()).isFalse(),
                () -> Assertions.assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }

    @Test
    @DisplayName("Apple OAuth 로그인 시 이미 가입된 회원일 경우 토큰과 이메일, 그리고 isRegistered 값을 true로 보낸다")
    void loginAppleOAuthRegisteredAndMocacongMember() {
        String expected = "kth@apple.com";
        String platformId = "1234321";
        Member member = new Member(
                expected,
                passwordEncoder.encode("a1b2c3d4"),
                "케이",
                null,
                Platform.APPLE,
                platformId
        );
        memberRepository.save(member);
        when(appleOAuthUserProvider.getApplePlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.appleOAuthLogin(new AppleLoginRequest("token"));

        assertAll(
                () -> Assertions.assertThat(actual.getToken()).isNotNull(),
                () -> Assertions.assertThat(actual.getEmail()).isEqualTo(expected),
                () -> Assertions.assertThat(actual.getIsRegistered()).isTrue(),
                () -> Assertions.assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }

    @Test
    @DisplayName("Apple OAuth 로그인 시 등록은 완료됐지만 회원가입 절차에 실패해 닉네임이 없으면 isRegistered 값을 false로 보낸다")
    void loginAppleOAuthRegisteredButNotMocacongMember() {
        String expected = "kth@apple.com";
        String platformId = "1234321";
        Member member = new Member("kth@apple.com", Platform.APPLE, "1234321");
        memberRepository.save(member);
        when(appleOAuthUserProvider.getApplePlatformMember(anyString()))
                .thenReturn(new OAuthPlatformMemberResponse(platformId, expected));

        OAuthTokenResponse actual = authService.appleOAuthLogin(new AppleLoginRequest("token"));

        assertAll(
                () -> Assertions.assertThat(actual.getToken()).isNotNull(),
                () -> Assertions.assertThat(actual.getEmail()).isEqualTo(expected),
                () -> Assertions.assertThat(actual.getIsRegistered()).isFalse(),
                () -> Assertions.assertThat(actual.getPlatformId()).isEqualTo(platformId)
        );
    }
}
