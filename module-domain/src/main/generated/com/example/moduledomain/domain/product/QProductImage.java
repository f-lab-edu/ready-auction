package com.example.moduledomain.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductImage is a Querydsl query type for ProductImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductImage extends EntityPathBase<ProductImage> {

    private static final long serialVersionUID = -239947416L;

    public static final QProductImage productImage = new QProductImage("productImage");

    public final com.example.moduledomain.domain.QBaseEntity _super = new com.example.moduledomain.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created_at = _super.created_at;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageFullPath = createString("imageFullPath");

    public final StringPath originalName = createString("originalName");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath savedName = createString("savedName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated_at = _super.updated_at;

    public QProductImage(String variable) {
        super(ProductImage.class, forVariable(variable));
    }

    public QProductImage(Path<? extends ProductImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductImage(PathMetadata metadata) {
        super(ProductImage.class, metadata);
    }

}

