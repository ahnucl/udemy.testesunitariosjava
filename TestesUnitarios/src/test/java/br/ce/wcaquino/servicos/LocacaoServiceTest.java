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
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;
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

//@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {

	@InjectMocks @Spy
	private LocacaoService service;
//	private static int testCounter;
	
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
//		service = new LocacaoService();
////		LocacaoDAO dao = new LocacaoDAOFake();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		spc = Mockito.mock(SPCService.class);
//		service.setSPCService(spc);
//		email = Mockito.mock(EmailService.class);
//		service.setEmailService(email);
		
//		service = PowerMockito.spy(service); // não tem PowerMock mais
		
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
//		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilme().comValor(5.0).agora());
		filmes.add(umFilme().comValor(7.0).agora());

		Mockito.doReturn(DataUtils.obterData(28,4,2017)).when(service).obterData();
		
		// Poderia usar:
		// List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		//verificacao
/**			
//		assertEquals("Algo deu errado - ", 1, 2);
//		assertThat(5, describedAs("um número igual a 5", is(5))); // adiciona uma descrição ao teste... talvez sirva para o mesmo uso que a linha acima
//		
//		assertThat(locacao.getValor(), is(5.0));
//		assertThat(locacao.getValor(), is(equalTo(5.0))); // leitura ainda melhor que o anterior
//		assertThat(locacao.getValor(), is(not(4.0)));
//		
//		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
//		assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		
//		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
//		assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true) );
*/		

//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true) );
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		
		error.checkThat(locacao.getValor(), is(equalTo(12.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	/**
	 * Forma elegante de testar esperando Exceções
	 * @throws Exception
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExecaoAoAlugarFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilmeSemEstoque().agora());
		filmes.add(umFilme().semEstoque().agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		System.out.println("Forma elegante");
		
	}
	
	
	/**
	 * Forma robusta de testar esperando Exceções
	 * @throws Exception
	 */
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
		/*}  catch (FilmeSemEstoqueException e) { // não é a exceção do teste, lança pro JUnit
			// TODO Auto-generated catch block
			e.printStackTrace(); */
		}  catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
		
		System.out.println("Forma robusta"); // apenas essa forma consegue imprimir após a exceção
	}
	
	/**
	 * Forma nova	
	 * @throws FilmeSemEstoqueException
	 * @throws LocadoraException
	 * Teste removido porque não há mais a possibilidade de um filme ser vazio, e sim da lista estar nula ou vazia
	 */

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
		List<Filme> filmes = new ArrayList<Filme>();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Lista de filmes nula");
		
		//acao
		service.alugarFilme(usuario, null);
	}

//	@Test
//	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 100.0),
//											new Filme("Filme 2", 2, 100.0),
//											new Filme("Filme 3", 2, 100.0));
//				
//		//acao
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		
//		//verificacao
//		//100+100+75=275
//		
//		assertThat(resultado.getValor(), is(275.0));
//	}
//	
//	@Test
//	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 100.0),
//											new Filme("Filme 2", 2, 100.0),
//											new Filme("Filme 3", 2, 100.0),
//											new Filme("Filme 4", 2, 100.0));
//				
//		//acao
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		
//		//verificacao
//		//100+100+75+50=325
//		
//		assertThat(resultado.getValor(), is(325.0));
//	}
//
//	@Test
//	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 100.0),
//											new Filme("Filme 2", 2, 100.0),
//											new Filme("Filme 3", 2, 100.0),
//											new Filme("Filme 4", 2, 100.0),
//											new Filme("Filme 5", 2, 100.0));
//				
//		//acao
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		
//		//verificacao
//		//100+100+75+50+25=350
//		
//		assertThat(resultado.getValor(), is(350.0));
//	}
//	
//	@Test
//	public void devePagar0NoFilme6() throws FilmeSemEstoqueException, LocadoraException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 100.0),
//											new Filme("Filme 2", 2, 100.0),
//											new Filme("Filme 3", 2, 100.0),
//											new Filme("Filme 4", 2, 100.0),
//											new Filme("Filme 5", 2, 100.0),
//											new Filme("Filme 6", 2, 100.0));
//				
//		// testes com arrays e lists
////		Integer[] a = new Integer[] {11111,56,15,88};
////		for(int i:a) System.out.println(i);
////		for(int i = 0; i < a.length; i++) System.out.println(a[i]);
////		List<Integer> b = Arrays.asList(a);
////		for(int i:b) System.out.println(i);
////		for(int i = 0; i < b.size(); i++) System.out.println(b.get(i));
//		
//		//acao
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		
//		
//		//verificacao
//		//100+100+75+50+25+0=350
//		
//		assertThat(resultado.getValor(), is(350.0));
//	}
//	
	@Test
	// @Ignore // esse teste está conflitante com o primeiro; funciona apenas no sábado;
	// ambos os testes não estão repetitíveis
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
				
		Mockito.doReturn(DataUtils.obterData( 29, 4, 2017)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
			// a linha abaixo que foi pra dentro do matcher
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
//		assertTrue(ehSegunda);
//		assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY)); // visibilidade ainda não chegou onde queriamos
//		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
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
//		verify(email, Mockito.times(2)).notificarAtrasao(usuario3);
		verify(email, atLeastOnce()).notificarAtrasao(usuario3);
		verify(email, never()).notificarAtrasao(usuario2);
		verifyNoMoreInteractions(email);
//		verifyZeroInteractions(spc); // o método de notificar atrasos não trabalha com o spc, PERMANECER NO ESCOPO DO CENÁRIOS
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
