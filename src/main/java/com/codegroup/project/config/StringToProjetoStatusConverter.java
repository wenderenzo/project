package com.codegroup.project.config;

import com.codegroup.project.projeto.entity.ProjetoStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProjetoStatusConverter implements Converter<String, ProjetoStatus> {

    @Override
    public ProjetoStatus convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return ProjetoStatus.fromValue(source);
    }
}

