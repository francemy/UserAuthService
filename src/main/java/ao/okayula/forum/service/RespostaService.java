package ao.okayula.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.RespostaModel;
import ao.okayula.forum.repository.RespostaRepository;

@Service
public class RespostaService {

    @Autowired
    private RespostaRepository respostaRepository;

    // Listar todas as respostas
    public List<RespostaModel> listarTodos() {
        return respostaRepository.findAll();
    }

    // Buscar respostas por tópico
    public List<RespostaModel> buscarPorTopico(Long topicoId) {
        return respostaRepository.findByTopicoId(topicoId);
    }

    // Buscar respostas por usuário
    public List<RespostaModel> buscarPorUsuario(Long usuarioId) {
        return respostaRepository.findByUsuarioId(usuarioId);
    }

    // Criar ou atualizar uma resposta
    public RespostaModel salvar(RespostaModel resposta) {
        return respostaRepository.save(resposta);
    }

    // Deletar uma resposta
    public void deletar(Long id) {
        respostaRepository.deleteById(id);
    }
}
