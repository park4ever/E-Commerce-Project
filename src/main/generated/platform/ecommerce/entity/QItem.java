package platform.ecommerce.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -569241483L;

    public static final QItem item = new QItem("item");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final EnumPath<ItemCategory> category = createEnum("category", ItemCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> discountPrice = createNumber("discountPrice", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isAvailable = createBoolean("isAvailable");

    public final StringPath itemName = createString("itemName");

    public final ListPath<ItemOption, QItemOption> itemOptions = this.<ItemOption, QItemOption>createList("itemOptions", ItemOption.class, QItemOption.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public QItem(String variable) {
        super(Item.class, forVariable(variable));
    }

    public QItem(Path<? extends Item> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItem(PathMetadata metadata) {
        super(Item.class, metadata);
    }

}

