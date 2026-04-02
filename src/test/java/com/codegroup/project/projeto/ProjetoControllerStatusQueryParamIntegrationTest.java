package com.codegroup.project.projeto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codegroup.project.common.api.ApiExceptionHandler;
import com.codegroup.project.config.StringToProjetoStatusConverter;
import com.codegroup.project.projeto.dto.ProjetoResponse;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

class ProjetoControllerStatusQueryParamIntegrationTest {

    private MockMvc mockMvc;
    private ProjetoService projetoService;

    @BeforeEach
    void setUp() {
        projetoService = mock(ProjetoService.class);
        ProjetoController controller = new ProjetoController(projetoService);

        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new StringToProjetoStatusConverter());

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new ApiExceptionHandler())
            .setConversionService(conversionService)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void deveRetornarBadRequestQuandoStatusForInvalido() throws Exception {
        mockMvc.perform(get("/projetos").param("status", "status inexistente"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(projetoService);
    }
}

