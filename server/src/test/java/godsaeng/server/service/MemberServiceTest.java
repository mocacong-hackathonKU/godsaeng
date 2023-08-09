package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.MemberProfileImage;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.IsDuplicateNicknameResponse;
import godsaeng.server.dto.response.MyPageResponse;
import godsaeng.server.exception.badrequest.DuplicateMemberException;
import godsaeng.server.exception.badrequest.DuplicateNicknameException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberProfileImageRepository;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.support.AwsS3Uploader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberProfileImageRepository memberProfileImageRepository;
    @Autowired
    private MemberService memberService;

    @MockBean
    private AwsS3Uploader awsS3Uploader;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원을 정상적으로 가입한다")
    void signUp() {
        String expected = "dlawotn3@naver.com";
        MemberSignUpRequest request = new MemberSignUpRequest(expected, "a1b2c3d4", "메리");

        memberService.signUp(request);

        List<Member> actual = memberRepository.findAll();
        Assertions.assertThat(actual).hasSize(1);
        Assertions.assertThat(actual.get(0).getEmail()).isEqualTo(expected);
    }

    @Test
    @DisplayName("이미 가입된 이메일이 존재하면 회원 가입 시에 예외를 반환한다")
    void signUpByDuplicateEmailMember() {
        String email = "dlawotn3@naver.com";
        memberRepository.save(new Member(email, "1234", "메리"));
        MemberSignUpRequest request = new MemberSignUpRequest(email, "a1b2c3d4", "메리");

        Assertions.assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(DuplicateMemberException.class);
    }

    @Test
    @DisplayName("이미 가입된 닉네임이 존재하면 회원 가입 시에 예외를 반환한다")
    void signUpByDuplicateNicknameMember() {
        String nickname = "메리";
        memberRepository.save(new Member("dlawotn2@naver.com", "1234", nickname));
        MemberSignUpRequest request = new MemberSignUpRequest("dlawotn3@naver.com", "a1b2c3d4",
                nickname);

        Assertions.assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(DuplicateNicknameException.class);
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

    @Test
    @DisplayName("회원의 프로필 이미지를 변경하면 s3 서버와 연동하여 이미지를 업로드한다")
    void updateProfileImg() throws IOException {
        String expected = "test_img.jpg";
        Member savedMember = memberRepository.save(new Member("dlawotn3@naver.com", Platform.KAKAO,
                "11111"));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "메리",
                Platform.KAKAO.getValue(), "11111");
        memberService.signUpByOAuthMember(request);
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/images/" + expected);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", expected, "jpg",
                fileInputStream);

        when(awsS3Uploader.uploadImage(mockMultipartFile)).thenReturn("test_img.jpg");
        memberService.updateProfileImage(savedMember.getId(), mockMultipartFile);

        Member actual = memberRepository.findById(savedMember.getId())
                .orElseThrow();
        assertAll(
                () -> Assertions.assertThat(actual.getImgUrl()).isEqualTo(expected),
                () -> Assertions.assertThat(actual.getMemberProfileImage().getIsUsed()).isTrue()
        );
    }

    @Test
    @DisplayName("회원이 프로필 이미지를 삭제하거나 null로 설정하면 프로필 이미지는 null로 설정된다")
    void updateProfileImgWithNull() {
        MemberProfileImage memberProfileImage = new MemberProfileImage("test.me.jpg");
        memberProfileImageRepository.save(memberProfileImage);
        Member member = memberRepository.save(new Member("dlawotn3@naver.com", memberProfileImage,
                Platform.KAKAO, "11111"));
        OAuthMemberSignUpRequest request = new OAuthMemberSignUpRequest(null, "메리",
                Platform.KAKAO.getValue(), "11111");
        memberService.signUpByOAuthMember(request);

        memberService.updateProfileImage(member.getId(), null);

        Member actual = memberRepository.findById(member.getId())
                .orElseThrow();
        assertAll(
                () -> Assertions.assertThat(actual.getImgUrl()).isNull(),
                () -> Assertions.assertThat(actual.getMemberProfileImage()).isNull()
        );
    }
}
