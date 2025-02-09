package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.ChallengeConverter;
import TtattaBackend.ttatta.converter.ItemConverter;
import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.service.ChallengeService.ChallengeCommandService;
import TtattaBackend.ttatta.service.ChallengeService.ChallengeQueryService;
import TtattaBackend.ttatta.service.ItemService.ItemCommandService;
import TtattaBackend.ttatta.web.dto.ChallengeRequestDTO;
import TtattaBackend.ttatta.web.dto.ChallengeResponeseDTO;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

    @Operation(summary = "챌린지 생성 api",
            description = "챌린지를 생성하는 api입니다.\n"
    + "header에 access token을 넣어주세요.")
    @PostMapping()
    public ApiResponse<ChallengeResponeseDTO.CreateChallengeResultDTO> createChallenge(
            @RequestBody @Valid ChallengeRequestDTO.CreateChallengeRequestDTO request
    ) {
        Challenges challenge = challengeCommandService.createChallenge(request);
        return ApiResponse.onSuccess(
                ChallengeConverter.toCreateChallengeResultDTO(challenge)
        );
    }

    @Operation(summary = "챌린지 성공 api",
            description = "챌린지의 상태를 성공으로 바꾸는 api입니다.\n"
                    + "header에 access token을 넣어주세요.")
    @PatchMapping("/{challengeId}")
    public ApiResponse<ChallengeResponeseDTO.SuccessChallengeResultDTO> successChallenge(
            @PathVariable Long challengeId
    ) {
        return ApiResponse.onSuccess(
                ChallengeConverter.toSuccessChallengeResultDTO(
                        challengeCommandService.successChallenge(challengeId)
                )
        );
    }

    @Operation(summary = "금일 챌린지 조회 api",
            description = "금일 챌린지를 조회하는 api입니다.\n"
                    + "header에 access token을 넣어주세요.")
    @GetMapping()
    public ApiResponse<ChallengeResponeseDTO.ChallengeListResultDTO> getChallenges() {
        return ApiResponse.onSuccess(
                ChallengeConverter.toChallengeListResultDTO(challengeQueryService.getChallenges())
        );
    }
}
