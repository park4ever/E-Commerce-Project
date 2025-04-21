package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.ItemOption;

import java.util.List;
import java.util.Optional;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {

    Optional<ItemOption> findByItemIdAndSizeLabel(Long itemId, String sizeLabel);

    List<ItemOption> findByItemId(Long itemId);

    List<ItemOption> findBySizeLabelContaining(String size);
}
