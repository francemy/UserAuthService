package ao.okayula.forum.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.CursoModel;
import ao.okayula.forum.repository.CursoRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    // Buscar todos os cursos
    public List<CursoModel> listarTodos() {
        return cursoRepository.findAll();
    }

    // Buscar curso por id
    public Optional<CursoModel> buscarPorId(Long id) {
        return cursoRepository.findById(id);
    }

    // Buscar curso por nome
    public Optional<CursoModel> buscarPorNome(String nome) {
        return cursoRepository.findByNome(nome);
    }

    // Criar ou atualizar um curso
    public CursoModel salvar(CursoModel curso) {
        return cursoRepository.save(curso);
    }

    // Deletar um curso
    public void deletar(Long id) {
        cursoRepository.deleteById(id);
    }
}
