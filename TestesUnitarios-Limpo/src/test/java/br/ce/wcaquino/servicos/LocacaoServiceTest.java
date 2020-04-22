package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprio.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprio.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprio.ehHojeComDiferencaDias;
//imports estáticos
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@InjectMocks @Spy
	private LocacaoService service;

	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		System.out.println("Inicializando 2...");
		CalculadoraTest.ordem.append("2");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizando 2...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora(), umFilme().comValor(7.0).agora());

		Mockito.doReturn(DataUtils.obterData(28,4,2017)).when(service).obterData();

		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		//verificacao		
		error.checkThat(locacao.getValor(), is(equalTo(12.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExecaoAoAlugarFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilmeSemEstoque().agora());
		filmes.add(umFilme().semEstoque().agora());
		
		//acao
		service.alugarFilme(usuario, filmes);
		
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilme().agora());
		filmes.add(umFilme().agora());
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			fail();
		}  catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
		
	}

	@Test
	public void testLocacao_listaVazia() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Lista de filmes vazia");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void testLocacao_listaNula() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Lista de filmes nula");
		
		//acao
		service.alugarFilme(usuario, null);
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
				
		Mockito.doReturn(DataUtils.obterData( 29, 4, 2017)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();

		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			
		//verificacao
			fail("Não lançou exceção");
		} catch (LocadoraException e) {
			assertThat(e.getMessage(),is("Usuario negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		
		List<Locacao> locacoesPendentes = Arrays.asList(
			umLocacao().comUsuario(usuario).atrasado().agora(),
			umLocacao().comUsuario(usuario2).agora(), // usuario2 não está com a data atrasada
			umLocacao().comUsuario(usuario3).atrasado().agora(),
			umLocacao().comUsuario(usuario3).atrasado().agora());
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoesPendentes);
					
		//acao
		service.notificarAtrasos();
		
		//verificacao
		verify(email, times(3)).notificarAtrasao(Mockito.any(Usuario.class));
		verify(email).notificarAtrasao(usuario);
		verify(email, atLeastOnce()).notificarAtrasao(usuario3);
		verify(email, never()).notificarAtrasao(usuario2);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha SPC"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);

	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();		
		Integer dias = 3;
		
		//acao
		service.prorrogarLocacao(locacao, dias.intValue());
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(locacao.getValor()*dias.intValue()));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(dias.intValue()));
		
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
	
		//acao
		// Apenas usando API do Java (Reflection)
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		
		//verificacao
		assertThat(valor, is(100.0));
	}
}
