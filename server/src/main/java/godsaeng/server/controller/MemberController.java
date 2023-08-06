package godsaeng.server.controller;

import godsaeng.server.dto.request.OAuthMemberSignUpRequest;
import godsaeng.server.dto.response.IsDuplicateNicknameResponse;
import godsaeng.server.dto.response.OAuthMemberSignUpResponse;
import godsaeng.server.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Members", description = "회원")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "OAuth 회원가입")
    @PostMapping("/oauth")
    public ResponseEntity<OAuthMemberSignUpResponse> signUp(@RequestBody @Valid OAuthMemberSignUpRequest request) {
        OAuthMemberSignUpResponse response = memberService.signUpByOAuthMember(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 닉네임 중복체크")
    @GetMapping("/check-duplicate/nickname")
    public ResponseEntity<IsDuplicateNicknameResponse> checkDuplicateNickname(@RequestParam String value) {
        IsDuplicateNicknameResponse response = memberService.isDuplicateNickname(value);
        return ResponseEntity.ok(response);
    }
}
