package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.ChallengeConverter;
import TtattaBackend.ttatta.converter.ItemConverter;
import TtattaBackend.ttatta.domain.Challenges;
import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.service.ChallengeService.ChallengeCommandService;
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

//    @Operation(summary = "아이템 구매 api",
//            description = "아이템을 구매하는 api입니다.\npath path variable 로 구매하려는 아이템 id를 받습니다.")
//    @PatchMapping("/{itemId}")
//    public ApiResponse<ItemResponseDTO.ItemBuyResultDTO> buy (@PathVariable Long itemId) {
//        return ApiResponse.onSuccess(itemCommandService.buyItem(itemId));
//    }
//
//    @Operation(summary = "아이템 착용 api",
//            description = "아이템을 착용하는 api 입니다.\n path variable로 착용하려는 아이템 id를 받습니다.")
//    @PatchMapping("/equip/{itemId}")
//    public ApiResponse<ItemResponseDTO.ItemEquipResultDTO> equip (@PathVariable Long itemId) {
//        return ApiResponse.onSuccess(itemCommandService.equipItem(itemId));
//    }
}
