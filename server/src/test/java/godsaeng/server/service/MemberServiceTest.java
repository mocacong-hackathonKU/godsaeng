package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.MemberProfileImage;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.IsDuplicateNicknameResponse;
import godsaeng.server.dto.response.MyPageResponse;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberProfileImageRepository;
import godsaeng.server.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberProfileImageRepository memberProfileImageRepository;
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

    @Test
    @DisplayName("이미 존재하는 닉네임인 경우 True를 반환한다")
    void isDuplicateNicknameReturnTrue() {
        String email = "dlawotn3@naver.com";
        String platformId = "1234321";
        String nickname = "메리";
        memberRepository.save(new Member(email, Platform.KAKAO, platformId));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, nickname,
                Platform.KAKAO.getValue(), platformId);
        memberService.signUpByOAuthMember(request);
        IsDuplicateNicknameResponse response = memberService.isDuplicateNickname(nickname);

        memberService.signUpByOAuthMember(request);

        Assertions.assertThat(response.isResult()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 닉네임인 경우 False를 반환한다")
    void isDuplicateNicknameReturnFalse() {
        String nickname = "메리";

        IsDuplicateNicknameResponse response = memberService.isDuplicateNickname(nickname);

        Assertions.assertThat(response.isResult()).isFalse();
    }

    @Test
    @DisplayName("마이페이지로 내 정보를 조회한다")
    void findMyInfo() {
        String imgUrl = "test_img.jpg";
        String email = "kth990303@naver.com";
        String nickname = "메리";
        MemberProfileImage memberProfileImage = new MemberProfileImage(imgUrl);
        memberProfileImageRepository.save(memberProfileImage);
        Member savedMember = memberRepository.save(new Member(email, memberProfileImage, Platform.KAKAO,
                "11111"));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "메리",
                Platform.KAKAO.getValue(), "11111");
        memberService.signUpByOAuthMember(request);

        MyPageResponse actual = memberService.findMyInfo(savedMember.getId());

        assertAll(
                () -> Assertions.assertThat(actual.getEmail()).isEqualTo(email),
                () -> Assertions.assertThat(actual.getImgUrl()).isEqualTo(imgUrl),
                () -> Assertions.assertThat(actual.getNickname()).isEqualTo(nickname)
        );
    }
}
