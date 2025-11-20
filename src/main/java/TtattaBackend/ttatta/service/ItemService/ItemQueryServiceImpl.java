package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.enums.BodyPart;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.repository.ItemRepository;
import TtattaBackend.ttatta.repository.OwnedItemRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;
import static TtattaBackend.ttatta.domain.enums.BodyPart.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryServiceImpl implements ItemQueryService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OwnedItemRepository ownedItemRepository;

    @Override
    public List<Items> getShopItem(Optional<BodyPart> bodyPart) {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        // bodyPart에 따른 아이템 조회
        if (bodyPart.isPresent()) {
            if (bodyPart.get() == HEAD)
                return itemRepository.getShopItemByBodyPartHead(user, bodyPart);
            else if (bodyPart.get() == EYES)
                return itemRepository.getShopItemByBodyPartEyes(user, bodyPart);
            else if (bodyPart.get() == TORSO)
                return itemRepository.getShopItemByBodyPartTorso(user, bodyPart);
            else if (bodyPart.get() == null)
                return itemRepository.getShopItem(user);
            else
                throw new ExceptionHandler(ErrorStatus.ITEM_BODYPART_QUERY_STRING_INVALID);
        } else
            return itemRepository.getShopItem(user);
    }

    @Override
    public List<OwnedItems> getEquippedItem() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return ownedItemRepository.findByUsersAndIsEquipped(user, true);
    }

    @Override
    public List<OwnedItems> getMyItem() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return ownedItemRepository.findByUsers(user);
    }
}
