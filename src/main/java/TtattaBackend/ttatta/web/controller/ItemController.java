package TtattaBackend.ttatta.web.controller;

import TtattaBackend.ttatta.apiPayload.ApiResponse;
import TtattaBackend.ttatta.service.ItemService.ItemCommandService;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemCommandService itemCommandService;

    @Operation(summary = "아이템 구매 api",
            description = "아이템을 구매하는 api입니다. request body로 point를 받고, path parameters로 구매하려는 아이템 id를 받습니다.")
    @PatchMapping("/{itemId}")
    public ApiResponse<ItemResponseDTO.ItemBuyResultDTO>buy(@PathVariable Long itemId) {
        return ApiResponse.onSuccess(itemCommandService.buyItem(itemId));
    }
}