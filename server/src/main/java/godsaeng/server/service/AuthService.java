package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.KakaoLoginRequest;
import godsaeng.server.dto.response.OAuthTokenResponse;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.security.auth.JwtTokenProvider;
import godsaeng.server.security.auth.OAuthPlatformMemberResponse;
import godsaeng.server.security.auth.kakao.KakaoOAuthUserProvider;
import lombok.RequiredArgsConstructor;

import godsaeng.server.dto.request.AuthLoginRequest;
import godsaeng.server.dto.response.TokenResponse;
import godsaeng.server.exception.badrequest.PasswordMismatchException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final KakaoOAuthUserProvider kakaoOAuthUserProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(AuthLoginRequest request) {
        Member findMember = memberRepository.findByEmailAndPlatform(request.getEmail(), Platform.GODSAENG)
                .orElseThrow(NotFoundMemberException::new);
        validatePassword(findMember, request.getPassword());

        String token = issueToken(findMember);

        return TokenResponse.from(token);
    }

    private String issueToken(final Member findMember) {
        return jwtTokenProvider.createToken(findMember.getId());
    }

    private void validatePassword(final Member findMember, final String password) {
        if (!passwordEncoder.matches(password, findMember.getPassword())) {
            throw new PasswordMismatchException();
        }
    }

    public OAuthTokenResponse kakaoOAuthLogin(KakaoLoginRequest request) {
        OAuthPlatformMemberResponse kakaoPlatformMember =
                kakaoOAuthUserProvider.getKakaoPlatformMember(request.getCode());
        return generateOAuthTokenResponse(
                Platform.KAKAO,
                kakaoPlatformMember.getEmail(),
                kakaoPlatformMember.getPlatformId()
        );
    }

    private OAuthTokenResponse generateOAuthTokenResponse(Platform platform, String email, String platformId) {
        return memberRepository.findIdByPlatformAndPlatformId(platform, platformId)
                .map(memberId -> {
                    Member findMember = memberRepository.findById(memberId)
                            .orElseThrow(NotFoundMemberException::new);
                    String token = issueToken(findMember);
                    // OAuth 로그인은 성공했지만 회원가입에 실패한 경우
                    if (!findMember.isRegisteredOAuthMember()) {
                        return new OAuthTokenResponse(token, findMember.getEmail(), false, platformId);
                    }
                    return new OAuthTokenResponse(token, findMember.getEmail(), true, platformId);
                })
                .orElseGet(() -> {
                    Member oauthMember = new Member(email, platform, platformId);
                    Member savedMember = memberRepository.save(oauthMember);
                    String token = issueToken(savedMember);
                    return new OAuthTokenResponse(token, email, false, platformId);
                });
    }
}
