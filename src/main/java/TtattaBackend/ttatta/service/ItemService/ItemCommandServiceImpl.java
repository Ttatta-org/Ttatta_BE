package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.converter.ItemConverter;
import TtattaBackend.ttatta.domain.Items;

import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.repository.ItemRepository;
import TtattaBackend.ttatta.repository.OwnedItemRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import TtattaBackend.ttatta.web.dto.ItemRequestDTO;
import TtattaBackend.ttatta.web.dto.ItemResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemCommandServiceImpl implements ItemCommandService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OwnedItemRepository ownedItemRepository;

    @Override
    @Transactional
    public Items makeItem(ItemRequestDTO.MakeItemDTO request) {
        Items newItem = ItemConverter.toItem(request);
        return itemRepository.save(newItem);
    }

    @Override
    @Transactional
    public ItemResponseDTO.ItemBuyResultDTO buyItem(Long itemId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow();

        Items item = itemRepository.findById(itemId)
                .orElseThrow();

        if(user.getPoint() < item.getCost()){
            throw new ExceptionHandler(ErrorStatus.USER_NOT_FOUND);
        }

        user.updatePoint(user.getPoint() - item.getCost());

        OwnedItems ownedItem = new OwnedItems(itemId,true, user, item);
        ownedItemRepository.save(ownedItem);

        return ItemConverter.toItemBuyResultDTO(user, item);
    }
}