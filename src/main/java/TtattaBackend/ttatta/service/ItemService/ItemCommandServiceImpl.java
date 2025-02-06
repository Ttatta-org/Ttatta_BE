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
import java.util.Optional;

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

        // 사용자가 해당 아이템을 이미 구매했는지 확인
        if (ownedItemRepository.findByUsersAndItems(user, item).isPresent()) {
            throw new ExceptionHandler(ErrorStatus.ITEM_ALREADY_BOUGHT);
        }

        if(user.getPoint() < item.getCost()){
            throw new ExceptionHandler(ErrorStatus.ITEM_NO_MONEY);
        }


        user.updatePoint(user.getPoint() - item.getCost());

        OwnedItems ownedItem = new OwnedItems(null,false, user, item);
        ownedItemRepository.save(ownedItem);


        return ItemConverter.toItemBuyResultDTO(user, item);
    }

    @Override
    @Transactional
    public ItemResponseDTO.ItemEquipResultDTO equipItem(Long itemId) {

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

    @Override
    public OwnedItems disrobeItem(Long itemId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.USER_NOT_FOUND));
        Items item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ITEM_NOT_FOUND));

        Optional<OwnedItems> ownedItems = ownedItemRepository.findByUsersAndItems(user, item);

        if (ownedItems.isEmpty()) { // 아이템 구매 안한 상태
            throw new ExceptionHandler(ErrorStatus.ITEM_NOT_BUY);
        }

        OwnedItems ownedItem = ownedItems.get();

        if (ownedItem.getIsEquipped()) { // 착용 된 상태
            ownedItem.setEquipped(false); // 착용 해제
            ownedItemRepository.save(ownedItem);
            return ownedItem;
        } else { // 착용 안한 상태
            throw new ExceptionHandler(ErrorStatus.ITEM_NOT_EQUIPPED);
        }
    }
}