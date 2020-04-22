package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@Parameter
	public List<Filme> filmes;
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Inicio 3...");
		CalculadoraTest.ordem.append("3");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finish 3...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
		
	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = umFilme().agora();
	private static Filme filme3 = umFilme().agora();
	private static Filme filme4 = umFilme().agora();
	private static Filme filme5 = umFilme().agora();
	private static Filme filme6 = umFilme().agora();
	private static Filme filme7 = umFilme().agora();
		
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			{Arrays.asList( filme1, filme2), 200.0, "2 Filmes: Sem Desconto"},
			{Arrays.asList( filme1, filme2, filme3), 275.0, "3 Filmes: 25%"}, // funciona porque o tipo é Object e tanto List como Integer herdam dela
			{Arrays.asList( filme1, filme2, filme3, filme4), 325.0, "4 Filmes: 50%"},
			{Arrays.asList( filme1, filme2, filme3, filme4, filme5), 350.0, "5 Filmes: 75%"},
			{Arrays.asList( filme1, filme2, filme3, filme4, filme5, filme6), 350.0, "6 Filmes: 100%"},
			{Arrays.asList( filme1, filme2, filme3, filme4, filme5, filme6, filme7), 450.0, "7 Filmes: Sem Desconto"}
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException, InterruptedException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
						
//		Thread.sleep(5000);
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		
		//verificacao
		//100+100+75+50+25=350
		assertThat(resultado.getValor(), is(valorLocacao));
	}
	
}
