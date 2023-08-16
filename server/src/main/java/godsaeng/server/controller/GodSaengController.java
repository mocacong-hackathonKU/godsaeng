package godsaeng.server.controller;

import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.request.ProofSaveRequest;
import godsaeng.server.dto.response.*;
import godsaeng.server.security.auth.LoginUserId;
import godsaeng.server.service.GodSaengService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Tag(name = "Godsaengs", description = "같생")
@RestController
@RequiredArgsConstructor
@RequestMapping("/godsaengs")
public class GodSaengController {

    private final GodSaengService godSaengService;

    @Operation(summary = "같생 등록")
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ResponseEntity<GodSaengSaveResponse> save(@LoginUserId Long memberId,
                                                     @RequestBody GodSaengSaveRequest request) {
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
    public ResponseEntity<Void> attendGodSaeng(@LoginUserId Long memberId, @PathVariable Long godSaengId) {
        godSaengService.attendGodSaeng(memberId, godSaengId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "같생 월간 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyGodSaengsResponse> findMonthlyGodSaeng(@LoginUserId Long memberId,
                                                    @RequestParam("date")
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                    LocalDate date) {
        MonthlyGodSaengsResponse monthlyGodSaeng = godSaengService.findMonthlyGodSaeng(memberId, date);
        return ResponseEntity.ok(monthlyGodSaeng);
    }

    @Operation(summary = "같생 일간 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/daily")
    public ResponseEntity<DailyGodSaengsResponse> findDailyGodSaeng(@LoginUserId Long memberId,
                                                                    @RequestParam("date")
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                    LocalDate date) {
        DailyGodSaengsResponse dailyGodSaeng = godSaengService.findDailyGodSaeng(memberId, date);
        return ResponseEntity.ok(dailyGodSaeng);
    }

    @Operation(summary = "같생 인증 등록")
    @SecurityRequirement(name = "JWT")
    @PostMapping(value = "/proof/{godSaengId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProofSaveResponse> saveProof(@LoginUserId Long memberId,
                                                       @PathVariable Long godSaengId,
                                                       @RequestPart ProofSaveRequest proof,
                                                       @RequestPart MultipartFile proofImg) {
        ProofSaveResponse response = godSaengService.saveProof(memberId, godSaengId, proofImg, proof);
        return ResponseEntity.ok(response);
    }
}
