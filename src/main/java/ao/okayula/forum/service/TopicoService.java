package ao.okayula.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.TopicoModel;
import ao.okayula.forum.repository.TopicoRepository;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    // Listar todos os tópicos
    public List<TopicoModel> listarTodos() {
        return topicoRepository.findAll();
    }

    // Buscar tópicos por curso
    public List<TopicoModel> buscarPorCurso(Long cursoId) {
        return topicoRepository.findByCursoId(cursoId);
    }

    // Buscar tópicos por usuário
    public List<TopicoModel> buscarPorUsuario(Long usuarioId) {
        return topicoRepository.findByUsuarioId(usuarioId);
    }

    // Criar ou atualizar um tópico
    public TopicoModel salvar(TopicoModel topico) {
        return topicoRepository.save(topico);
    }

    // Deletar um tópico
    public void deletar(Long id) {
        topicoRepository.deleteById(id);
    }
}
