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

    private static final List<Integer> ALLOWED_SIZES = List.of(8, 12, 16, 20);
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

                        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

                        if (pageable == null) {
                            return PageRequest.of(0, DEFAULT_SIZE);
                        }

                        int size = pageable.getPageSize();
                        int sanitizedSize = ALLOWED_SIZES.contains(size) ? size : DEFAULT_SIZE;

                        return PageRequest.of(
                                pageable.getPageNumber(),
                                sanitizedSize,
                                pageable.getSort()
                        );
                    }
                };

        resolver.setFallbackPageable(PageRequest.of(0, DEFAULT_SIZE));
        resolver.setMaxPageSize(20);

        resolvers.add(resolver);
    }
}

