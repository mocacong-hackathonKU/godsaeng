package godsaeng.server.service;

import godsaeng.server.domain.Member;
import godsaeng.server.domain.MemberProfileImage;
import godsaeng.server.domain.Platform;
import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.*;
import godsaeng.server.exception.badrequest.*;
import godsaeng.server.exception.notfound.NotFoundMemberException;
import godsaeng.server.repository.MemberProfileImageRepository;
import godsaeng.server.repository.MemberRepository;
import godsaeng.server.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
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
    private final AwsS3Uploader awsS3Uploader;

    private final PasswordEncoder passwordEncoder;

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
    public OAuthMemberSignUpResponse signUpByOAuthMember(OAuthMemberSignUpRequest request) {
        Platform platform = Platform.from(request.getPlatform());
        Member member = memberRepository.findByPlatformAndPlatformId(platform, request.getPlatformId())
                .orElseThrow(NotFoundMemberException::new);

        member.registerOAuthMember(request.getEmail(), request.getNickname());
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
}
