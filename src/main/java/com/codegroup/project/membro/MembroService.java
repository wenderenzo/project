package com.codegroup.project.membro;

import com.codegroup.project.common.exception.NotFoundException;
import com.codegroup.project.membro.dto.MembroRequest;
import com.codegroup.project.membro.dto.MembroResponse;
import com.codegroup.project.membro.entity.Membro;
import com.codegroup.project.membro.repository.MembroRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembroService {

    private final MembroRepository membroRepository;

    public MembroService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Transactional
    public MembroResponse criar(MembroRequest request) {
        Membro membro = new Membro();
        membro.setNome(request.nome());
        membro.setAtribuicao(request.atribuicao());
        return toResponse(membroRepository.save(membro));
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> listar() {
        return membroRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MembroResponse buscarPorId(Long id) {
        Membro membro = buscarEntidade(id);
        return toResponse(membro);
    }

    @Transactional(readOnly = true)
    public Membro buscarEntidade(Long id) {
        return membroRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Membro nao encontrado: " + id));
    }

    private MembroResponse toResponse(Membro membro) {
        return new MembroResponse(membro.getId(), membro.getNome(), membro.getAtribuicao());
    }
}

