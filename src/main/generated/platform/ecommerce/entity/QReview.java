package platform.ecommerce.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -1335903174L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath adminReply = createString("adminReply");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isVisible = createBoolean("isVisible");

    public final QItem item;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMember member;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new QItem(forProperty("item")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

