package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;

public interface ItemCommandService {
    Items makeItem(ItemRequestDTO.MakeItemDTO request);
    ItemResponseDTO.ItemBuyResultDTO buyItem(Long itemId);
    ItemResponseDTO.ItemEquipResultDTO equipItem(Long itemId);
}
