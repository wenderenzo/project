package com.codegroup.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToProjetoStatusConverter statusConverter;

    public WebMvcConfig(StringToProjetoStatusConverter statusConverter) {
        this.statusConverter = statusConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(statusConverter);
    }
}

