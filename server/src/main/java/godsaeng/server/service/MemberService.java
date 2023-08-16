package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.MemberProfileImage;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.IsDuplicateNicknameResponse;
import godsaeng.server.dto.response.MemberSignUpResponse;
import godsaeng.server.dto.response.MyPageResponse;
import godsaeng.server.dto.response.OAuthMemberSignUpResponse;
import godsaeng.server.exception.badrequest.DuplicateMemberException;
import godsaeng.server.exception.badrequest.DuplicateNicknameException;
import godsaeng.server.exception.badrequest.InvalidNicknameException;
import godsaeng.server.exception.badrequest.InvalidPasswordException;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.GodSaengRepository;
import godsaeng.server.repository.MemberProfileImageRepository;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.service.event.DeleteMemberEvent;
import godsaeng.server.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$");

    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final GodSaengRepository godSaengRepository;
    private final AwsS3Uploader awsS3Uploader;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public MemberSignUpResponse signUp(MemberSignUpRequest request) {
        validatePassword(request.getPassword());
        validateDuplicateMember(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        try {
            Member member = new Member(request.getEmail(), encodedPassword, request.getNickname());
            return new MemberSignUpResponse(memberRepository.save(member).getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateMemberException();
        }
    }

    private void validateDuplicateMember(MemberSignUpRequest memberSignUpRequest) {
        if (memberRepository.existsByEmailAndPlatform(memberSignUpRequest.getEmail(), Platform.GODSAENG)) {
            throw new DuplicateMemberException();
        }
        validateDuplicateNickname(memberSignUpRequest.getNickname());
    }

    private void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))
            throw new DuplicateNicknameException();
    }

    private void validatePassword(String password) {
        if (!PASSWORD_REGEX.matcher(password).matches()) {
            throw new InvalidPasswordException();
        }
    }

    @Transactional
    public OAuthMemberSignUpResponse signUpByOAuthMember(OAuthMemberSignUpRequest request, MultipartFile profileImg) {
        Platform platform = Platform.from(request.getPlatform());
        Member member = memberRepository.findByPlatformAndPlatformId(platform, request.getPlatformId())
                .orElseThrow(NotFoundMemberException::new);

        String profileImgUrl = awsS3Uploader.uploadImage(profileImg);
        MemberProfileImage memberProfileImage = new MemberProfileImage(profileImgUrl, true);
        memberProfileImageRepository.save(memberProfileImage);
        member.registerOAuthMember(request.getEmail(), request.getNickname(), memberProfileImage);
        return new OAuthMemberSignUpResponse(member.getId());
    }

    public IsDuplicateNicknameResponse isDuplicateNickname(String nickname) {
        validateNickname(nickname);

        Boolean isPresent = memberRepository.existsByNickname(nickname);
        return new IsDuplicateNicknameResponse(isPresent);
    }

    private void validateNickname(String nickname) {
        if (nickname.isBlank()) {
            throw new InvalidNicknameException();
        }
    }

    public MyPageResponse findMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        return new MyPageResponse(member.getEmail(), member.getNickname(), member.getImgUrl());
    }

    @Transactional
    public void updateProfileImage(Long memberId, MultipartFile profileImg) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        if (profileImg == null) {
            member.updateProfileImgUrl(null);
            return;
        }
        String profileImgUrl = awsS3Uploader.uploadImage(profileImg);
        MemberProfileImage memberProfileImage = new MemberProfileImage(profileImgUrl, true);
        memberProfileImageRepository.save(memberProfileImage);
        member.updateProfileImgUrl(memberProfileImage);
    }

    @Transactional
    public void delete(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);
        applicationEventPublisher.publishEvent(new DeleteMemberEvent(findMember));

        memberRepository.delete(findMember);
    }
}
