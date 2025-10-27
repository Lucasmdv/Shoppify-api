package org.stockify.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CarouselItem {

    @Column(name = "url", length = 512)
    private String url;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "href", length = 512)
    private String href;
}
