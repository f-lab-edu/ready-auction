package com.example.moduledomain.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductLike is a Querydsl query type for ProductLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductLike extends EntityPathBase<ProductLike> {

    private static final long serialVersionUID = -561843734L;

    public static final QProductLike productLike = new QProductLike("productLike");

    public final com.example.moduledomain.domain.QBaseEntity _super = new com.example.moduledomain.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created_at = _super.created_at;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated_at = _super.updated_at;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QProductLike(String variable) {
        super(ProductLike.class, forVariable(variable));
    }

    public QProductLike(Path<? extends ProductLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductLike(PathMetadata metadata) {
        super(ProductLike.class, metadata);
    }

}

