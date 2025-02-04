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

        // 사용자가 해당 아이템을 이미 구매했는지 확인
        if (ownedItemRepository.findByUsersAndItems(user, item).isPresent()) {
            throw new ExceptionHandler(ErrorStatus.ITEM_ALREADY_BOUGHT);
        }

        user.updatePoint(user.getPoint() - item.getCost());

        OwnedItems ownedItem = new OwnedItems(null,false, user, item);
        ownedItemRepository.save(ownedItem);


        return ItemConverter.toItemBuyResultDTO(user, item);
    }

    @Override
    @Transactional
    public ItemResponseDTO.ItemEquipResultDTO equipItem(Long itemId) {

        // 사용자 관련 에러 처리 어떻게 해야할지 모르겠어요...
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));


        // 아이템 엔티티 조회
        Items item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ITEM_NOT_FOUND));

        OwnedItems ownedItem = ownedItemRepository.findByItems(item)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ITEM_NOT_FOUND));

        ownedItem.setEquipped(true);
        ownedItemRepository.save(ownedItem);

        return ItemConverter.toItemEquipDTO(ownedItem);
    }
}