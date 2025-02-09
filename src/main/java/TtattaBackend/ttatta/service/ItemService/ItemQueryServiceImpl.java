package TtattaBackend.ttatta.service.ItemService;

import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import TtattaBackend.ttatta.config.security.SecurityUtil;
import TtattaBackend.ttatta.domain.Items;
import TtattaBackend.ttatta.domain.Users;
import TtattaBackend.ttatta.domain.mapping.OwnedItems;
import TtattaBackend.ttatta.repository.ItemRepository;
import TtattaBackend.ttatta.repository.OwnedItemRepository;
import TtattaBackend.ttatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemQueryServiceImpl implements ItemQueryService{

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final OwnedItemRepository ownedItemRepository;

    @Override
    public List<Items> getShopItem() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

         return itemRepository.getShopItem(user);
    }

    @Override
    public List<Items> getEquippedItem() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return itemRepository.findByIsEquipped(user);
    }

    @Override
    public List<OwnedItems> getMyItem() {
        Long userId = SecurityUtil.getCurrentUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(USER_NOT_FOUND));

        return ownedItemRepository.findByUsers(user);
    }
}
