package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;
	
	private UsuarioBuilder() {};
	
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario();
		builder.usuario.setNome("Usuario1");
		return builder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		this.usuario.setNome(nome);
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}
}
