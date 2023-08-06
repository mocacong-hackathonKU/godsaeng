package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.OAuthMemberSignUpResponse;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public OAuthMemberSignUpResponse signUpByOAuthMember(OAuthMemberSignUpRequest request) {
        Platform platform = Platform.from(request.getPlatform());
        Member member = memberRepository.findByPlatformAndPlatformId(platform, request.getPlatformId())
                .orElseThrow(NotFoundMemberException::new);

        member.registerOAuthMember(request.getEmail(), request.getNickname());
        return new OAuthMemberSignUpResponse(member.getId());
    }
}
