package com.example.moduledomain.domain.product;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS("전자제품"),
    FASHION("패션"),
    FURNITURE("가구"),
    HOME_APPLIANCES("가전제품"),
    BOOKS("도서"),
    TOYS("장난감"),
    SPORTS("스포츠"),
    CAR("자동차"),
    ART("미술품"),
    JEWELRY("보석"),
    BEAUTY("뷰티"),
    MUSIC_INSTRUMENTS("악기"),
    OTHER("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
