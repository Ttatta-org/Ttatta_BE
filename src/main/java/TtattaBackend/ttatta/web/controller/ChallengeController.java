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

    @Operation(summary = "가장 최근dp 실패한 챌린지 5개 조회 api",
            description = "가장 최근에 실패한 5개의 챌린지를 조회하는 api입니다.\n"
                    + "header에 access token을 넣어주세요.")
    @GetMapping("/fail")
    public ApiResponse<ChallengeResponeseDTO.FailChallengeListResultDTO> getFailChallenges() {
        return ApiResponse.onSuccess(
                ChallengeConverter.toFailChallengeListResultDTO(challengeQueryService.getFailChallenges())
        );
    }

}
