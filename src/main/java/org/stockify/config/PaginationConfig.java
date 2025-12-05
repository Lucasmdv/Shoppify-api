package org.stockify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PaginationConfig implements WebMvcConfigurer {

    private static final List<Integer> ALLOWED_SIZES = List.of(1, 2, 3, 4, 5, 8, 12, 16, 20);
    private static final int DEFAULT_SIZE = 12;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        PageableHandlerMethodArgumentResolver resolver =
                new PageableHandlerMethodArgumentResolver() {

                    @Override
                    public Pageable resolveArgument(MethodParameter methodParameter,
                                                    ModelAndViewContainer mavContainer,
                                                    NativeWebRequest webRequest,
                                                    WebDataBinderFactory binderFactory) {

                        Pageable pageable = (Pageable) super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

                        if (pageable == null) {
                            return PageRequest.of(0, DEFAULT_SIZE);
                        }
                        int sanitizedSize = ALLOWED_SIZES.contains(pageable.getPageSize()) ? pageable.getPageSize() : DEFAULT_SIZE;

                        int sanitizedPage = pageable.getPageNumber() >= 0 ? pageable.getPageNumber() : 0;
                        return PageRequest.of(sanitizedPage, sanitizedSize, pageable.getSort());
                    }
                };

        resolver.setFallbackPageable(PageRequest.of(0, DEFAULT_SIZE));
        resolver.setMaxPageSize(ALLOWED_SIZES.stream().max(Integer::compareTo).orElse(DEFAULT_SIZE));

        resolvers.add(resolver);
    }
}

