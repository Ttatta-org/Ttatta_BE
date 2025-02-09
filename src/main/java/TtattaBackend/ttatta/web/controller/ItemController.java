package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.converter.ItemConverter;
import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.service.ItemService.ItemCommandService;
import TtattaBackend.ttatta.service.ItemService.ItemQueryService;
import TtattaBackend.ttatta.service.UserService.UserCommandService;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemCommandService itemCommandService;

    private final ItemQueryService itemQueryService;

    private final UserCommandService userCommandService;

    @Operation(summary = "아이템 생성 api",
            description = "아이템을 생성하는 api 에요. 아자아자 파이팅")
    @PostMapping()
    public ApiResponse<ItemResponseDTO.MakeItemResultDTO> makeItem (@RequestBody ItemRequestDTO.MakeItemDTO request) {
        Items item = itemCommandService.makeItem(request);
        return ApiResponse.onSuccess(ItemConverter.toMakeItemResultDTO(item));
    }

    @Operation(summary = "아이템 구매 api",
            description = "아이템을 구매하는 api입니다.\npath path variable 로 구매하려는 아이템 id를 받습니다.")
    @PatchMapping("/{itemId}")
    public ApiResponse<ItemResponseDTO.ItemBuyResultDTO> buy (@PathVariable Long itemId) {
        return ApiResponse.onSuccess(itemCommandService.buyItem(itemId));
    }

    @Operation(summary = "아이템 착용 api",
            description = "아이템을 착용하는 api 입니다.\n path variable로 착용하려는 아이템 id를 받습니다.")
    @PatchMapping("/equip/{itemId}")
    public ApiResponse<ItemResponseDTO.ItemEquipResultDTO> equip (@PathVariable Long itemId) {
        return ApiResponse.onSuccess(itemCommandService.equipItem(itemId));
    }

    @Operation(summary = "아이템 해제 api",
            description = "아이템을 해제하는 api 입니다.\n path variable로 해제하려는 아이템 id를 받습니다.")
    @PatchMapping("/disrobe/{itemId}")
    public ApiResponse<ItemResponseDTO.ItemDisrobeResultDTO> disrobe (@PathVariable Long itemId) {

        OwnedItems ownedItems = itemCommandService.disrobeItem(itemId);

        return ApiResponse.onSuccess(
                ItemConverter.toItemDisrobeDTO(ownedItems)
        );
    }

    @Operation(summary = "미소유 아이템 (shop) api",
            description = "shop 화면에서 사용자가 구매하지 않은 아이템을 반환하는 API 입니다.")
    @GetMapping("/shop")
    public ApiResponse<ItemResponseDTO.ItemShopListDTO> shop () {
        List<Items> itemsList = itemQueryService.getShopItem();
        Long point = userCommandService.getUserPoint();

        return ApiResponse.onSuccess(
                ItemConverter.toItemShopListDTO(itemsList, point)
        );
    }

    @Operation(summary = "착용한 아이템 조회 api",
            description = "기록하기 버튼, 챌린지 화면, shop 등 화면에서 캐릭터가 입은 옷의 id를 반환하는 API 입니다.")
    @GetMapping("/equipped")
    public ApiResponse<ItemResponseDTO.IdListDTO> equipped() {
        List<Items> itemsList = itemQueryService.getEquippedItem();
        return ApiResponse.onSuccess(
                ItemConverter.toIdListDTO(itemsList)
        );
    }
}