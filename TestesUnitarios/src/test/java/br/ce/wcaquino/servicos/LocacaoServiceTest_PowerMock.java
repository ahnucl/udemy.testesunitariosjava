package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprio.caiNumaSegunda;
//imports estáticos
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTest_PowerMock {

	@InjectMocks
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
		service = PowerMockito.spy(service);
		System.out.println("Iniciando 4...");
		CalculadoraTest.ordem.append(4);
	}
	
	@After
	public void tearDown() {
		System.out.println("Finish 4...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH,28);
//		calendar.set(Calendar.MONTH,Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilme().comValor(5.0).agora());
		filmes.add(umFilme().comValor(7.0).agora());
				
		// Poderia usar:
		// List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		System.out.println("Teste!");
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(12.0)));	
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
				
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH,29);
//		calendar.set(Calendar.MONTH,Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
			// a linha abaixo que foi pra dentro do matcher
		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);

		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
//		PowerMockito.verifyStatic(Mockito.times(2));
//		Calendar.getInstance();
	}
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
		
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
	
		//acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificacao
		assertThat(valor, is(100.0));
	}
}
