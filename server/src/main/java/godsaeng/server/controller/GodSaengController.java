package godsaeng.server.controller;

import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.dto.response.GodSaengsResponse;
import godsaeng.server.security.auth.LoginUserId;
import godsaeng.server.service.GodSaengService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Godsaengs", description = "같생")
@RestController
@RequiredArgsConstructor
@RequestMapping("/godsaeng")
public class GodSaengController {

    private final GodSaengService godSaengService;

    @Operation(summary = "같생 등록")
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ResponseEntity<GodSaengSaveResponse> save(@LoginUserId Long memberId, @RequestBody GodSaengSaveRequest request) {
        GodSaengSaveResponse response = godSaengService.save(memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "같생 전부 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<GodSaengsResponse> findAllGodSaeng(@LoginUserId Long memberId) {
        GodSaengsResponse response = godSaengService.findAllGodSaeng();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "같생 참가 신청")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/attend/{godSaengId}")
    public ResponseEntity<Void> attendGodSaeng(@LoginUserId Long memberId,@PathVariable Long godSaengId) {
        godSaengService.attendGodSaeng(memberId, godSaengId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "같생 인증 등록")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/proof/{godSaengId}")
    public ResponseEntity<Void> saveProof(@LoginUserId Long memberId,@PathVariable Long godSaengId) {
        godSaengService.saveProof(memberId, godSaengId);
        return ResponseEntity.ok().build();
    }
}
