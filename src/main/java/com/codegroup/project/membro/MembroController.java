package com.codegroup.project.membro;

import com.codegroup.project.membro.dto.MembroRequest;
import com.codegroup.project.membro.dto.MembroResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membros-externos")
public class MembroController {

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembroResponse criar(@Valid @RequestBody MembroRequest request) {
        return membroService.criar(request);
    }

    @GetMapping
    public List<MembroResponse> listar() {
        return membroService.listar();
    }

    @GetMapping("/{id}")
    public MembroResponse buscarPorId(@PathVariable Long id) {
        return membroService.buscarPorId(id);
    }
}

