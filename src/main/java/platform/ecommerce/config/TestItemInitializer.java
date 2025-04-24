package platform.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import platform.ecommerce.entity.Item;
import platform.ecommerce.repository.ItemRepository;

//@Configuration
@RequiredArgsConstructor
public class TestItemInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (itemRepository.count() > 10) {
            System.out.println("⚠️ 이미 상품이 10개 이상 있어, 테스트 데이터 생성을 건너뜁니다. 현재 상품 수 : " + itemRepository.count());
            return;
        }

        for (int i = 1; i <= 50; i++) {
            Item item = Item.builder()
                    .itemName("테스트용 상품 " + i)
                    .description("테스트용 상품 " + i + "의 상품 설명입니다.")
                    .price(i * 10000)
//                    .stockQuantity(i % 20 + 1)
                    .imageUrl("/item/default.png") //이렇게 X
                    .isAvailable(true)
                    .build();

            itemRepository.save(item);
        }

        System.out.println("✅ 테스트 상품 50명 생성 완료!");
    }
}
