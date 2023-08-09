package godsaeng.server.controller;

import godsaeng.server.dto.request.GodSaengSaveRequest;
import godsaeng.server.dto.response.GodSaengSaveResponse;
import godsaeng.server.security.auth.LoginUserId;
import godsaeng.server.service.GodSaengService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
