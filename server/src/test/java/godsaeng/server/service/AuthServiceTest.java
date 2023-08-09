package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.response.TokenResponse;
import godsaeng.server.exception.badrequest.PasswordMismatchException;
import godsaeng.server.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

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
}
