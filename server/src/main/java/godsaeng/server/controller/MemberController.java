package godsaeng.server.controller;

import godsaeng.server.dto.request.MemberSignUpRequest;
import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.IsDuplicateNicknameResponse;
import godsaeng.server.dto.response.MemberSignUpResponse;
import godsaeng.server.dto.response.MyPageResponse;
import godsaeng.server.dto.response.OAuthMemberSignUpResponse;
import godsaeng.server.security.auth.LoginUserId;
import godsaeng.server.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


@Tag(name = "Members", description = "회원")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "이메일 회원가입")
    @PostMapping
    public ResponseEntity<MemberSignUpResponse> signUp(@RequestBody @Valid MemberSignUpRequest request) {
        MemberSignUpResponse response = memberService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 회원가입")
    @PostMapping("/oauth")
    public ResponseEntity<OAuthMemberSignUpResponse> signUp(@RequestPart @Valid OAuthMemberSignUpRequest request,
                                                            @RequestPart MultipartFile file) {
        OAuthMemberSignUpResponse response = memberService.signUpByOAuthMember(request, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 닉네임 중복체크")
    @GetMapping("/check-duplicate/nickname")
    public ResponseEntity<IsDuplicateNicknameResponse> checkDuplicateNickname(@RequestParam String value) {
        IsDuplicateNicknameResponse response = memberService.isDuplicateNickname(value);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이페이지 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> findMyInfo(@LoginUserId Long memberId) {
        MyPageResponse response = memberService.findMyInfo(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이페이지 - 프로필 이미지 수정")
    @SecurityRequirement(name = "JWT")
    @PutMapping(value = "/mypage/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileImage(
            @LoginUserId Long memberId,
            @RequestParam(value = "file", required = false) MultipartFile multipartFile
    ) {
        memberService.updateProfileImage(memberId, multipartFile);
        return ResponseEntity.ok().build();
    }
}
