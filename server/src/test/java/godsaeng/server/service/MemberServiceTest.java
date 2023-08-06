package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("OAuth 유저 로그인 후 정보를 입력받아 회원을 가입한다")
    void signUpByOAuthMember() {
        String email = "dlawotn3@naver.com";
        String platformId = "1234321";
        Member savedMember = memberRepository.save(new Member(email, Platform.KAKAO, platformId));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "메리",
                Platform.KAKAO.getValue(), platformId);

        memberService.signUpByOAuthMember(request);

        Member actual = memberRepository.findById(savedMember.getId()).orElseThrow();
        assertThat(actual.getNickname()).isEqualTo("메리");
    }

    @Test
    @DisplayName("OAuth 유저 로그인 후 회원가입 시 platform과 platformId 정보로 회원이 존재하지 않으면 예외를 반환한다")
    void signUpByOAuthMemberWhenInvalidPlatformInfo() {
        memberRepository.save(new Member("dlawotn3@naver.com", Platform.KAKAO, "1234321"));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "메리",
                Platform.KAKAO.getValue(), "invalid");

        assertThatThrownBy(() -> memberService.signUpByOAuthMember(request))
                .isInstanceOf(NotFoundMemberException.class);
    }
}
