package one.digitalInovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalInovation.gof.model.Cliente;
import one.digitalInovation.gof.model.ClienteRepository;
import one.digitalInovation.gof.model.Endereco;
import one.digitalInovation.gof.model.EnderecoRepository;
import one.digitalInovation.gof.service.ClienteService;
import one.digitalInovation.gof.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService {
	
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private ViaCepService viaCepService; //client HTTP

	@Override
	public Iterable<Cliente> buscarTodos() {
		//buscar todos os clientes
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		//buscar cliente por ID
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
		
		
	}

	private void salvarClienteComCep(Cliente cliente) {
		// TODO verificar se o endereço do cliente existe pelo cep
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			//caso não exista, integrar com ViaCep e persistir o retorno
			Endereco novoEndereco = viaCepService.consultarCep(cep); //chamada do client http pelo Feign
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		//Inserir cliente, vinculando endereço (novo ou existente)
		clienteRepository.save(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		//Buscar cliente por ID caso exista
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if(clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
		
	}

	@Override
	public void deletar(Long id) {
		//deletar cliente pelo ID
		clienteRepository.deleteById(id);
		
	}
	
	
}
