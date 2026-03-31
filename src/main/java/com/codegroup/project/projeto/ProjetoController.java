package com.codegroup.project.projeto;

import com.codegroup.project.projeto.dto.AssociarMembrosRequest;
import com.codegroup.project.projeto.dto.ProjetoRequest;
import com.codegroup.project.projeto.dto.ProjetoResponse;
import com.codegroup.project.projeto.dto.RelatorioProjetosResponse;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjetoResponse criar(@Valid @RequestBody ProjetoRequest request) {
        return projetoService.criar(request);
    }

    @GetMapping("/{id}")
    public ProjetoResponse buscarPorId(@PathVariable Long id) {
        return projetoService.buscarPorId(id);
    }

    @GetMapping
    public Page<ProjetoResponse> listar(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) ProjetoStatus status,
        @RequestParam(required = false) Long gerenteId,
        Pageable pageable
    ) {
        return projetoService.listar(nome, status, gerenteId, pageable);
    }

    @PutMapping("/{id}")
    public ProjetoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProjetoRequest request) {
        return projetoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        projetoService.excluir(id);
    }

    @PostMapping("/{id}/membros")
    public ProjetoResponse associarMembros(
        @PathVariable Long id,
        @Valid @RequestBody AssociarMembrosRequest request
    ) {
        return projetoService.associarMembros(id, request.membroIds());
    }

    @GetMapping("/relatorio")
    public RelatorioProjetosResponse relatorio() {
        return projetoService.relatorio();
    }
}

