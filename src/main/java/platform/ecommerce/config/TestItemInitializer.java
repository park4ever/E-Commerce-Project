package platform.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.ItemCategory;
import platform.ecommerce.entity.ItemOption;
import platform.ecommerce.repository.ItemRepository;

import java.util.List;
import java.util.Set;

import static platform.ecommerce.entity.ItemCategory.*;

@Configuration
@RequiredArgsConstructor
public class TestItemInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (itemRepository.count() > 10) {
            System.out.println("⚠️ 이미 상품이 충분히 존재하여 테스트 데이터 생성을 건너뜁니다. 현재 상품 수 : " + itemRepository.count());
            return;
        }

        int countPerCategory = 5;
        int sequence = 1;

        for (ItemCategory category : values()) {
            for (int i = 1; i <= countPerCategory; i++) {
                Item item = Item.builder()
                        .itemName("[" + category.name() + "]" + " 상품 " + i)
                        .description("[" + category.name() + "]" + " 테스트용 상품입니다.")
                        .price(100000 + (i * 10000))
                        .category(category)
                        .imageUrl("/images/item/default.png")
                        .isAvailable(true)
                        .build();

                addOptionsByCategory(item, category, sequence);
                itemRepository.save(item);
                sequence++;
            }
        }

        System.out.println("✅ 카테고리별 테스트 데이터 생성이 완료되었습니다.");
    }

    private void addOptionsByCategory(Item item, ItemCategory category, int baseStock) {
        if (Set.of(TOP, BOTTOM, OUTER).contains(category)) {
            for (String size : List.of("S", "M", "L", "XL")) {
                item.addItemOption(ItemOption.create(item, size, (baseStock % 10) + 1));
            }
        } else if (category == SHOES) {
            for (int size = 240; size <= 300; size += 5) {
                item.addItemOption(ItemOption.create(item, String.valueOf(size), (baseStock % 5) + 1));
            }
        } else {
            item.addItemOption(ItemOption.create(item, "OS", 10));
        }
    }
}
