package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZero;

public class CalculadoraTest {

	public static StringBuffer ordem = new StringBuffer();
	
	private Calculadora calculadora;
	
	@Before
	public void setup() {
		calculadora = new Calculadora();
		System.out.println("Start 1...");
		ordem.append("1");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finish 1...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(ordem.toString());
	}
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 5;
		int b = 125;
		
		//acao
		int resultado = calculadora.somar(a, b);
		
		//verificacao
		Assert.assertEquals(130, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//cenario
		int a = 8;
		int b = 3;
		
		//acao
		int resultado = calculadora.subtrair(a,b);
		
		//verificacao
		Assert.assertEquals(5, resultado);
		
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZero {
		//cenario
		int a = 8;
		int b = 4;
		
		//acao
		int resultado = calculadora.dividir(a,b);
		
		//verificacao
		Assert.assertEquals(2, resultado);
		
	}
	
	@Test(expected=NaoPodeDividirPorZero.class)
	public void deveLancarExcecaoSeDividirPorZero() throws NaoPodeDividirPorZero {
		//cenario
		int a = 8;
		int b = 0;
		
		//acao
		calculadora.dividir(a,b);
	}
	
	@Test
	public void deveDividir() {
		String a = "6";
		String b = "3";
		
		int resultado = calculadora.divide(a, b);
		
		assertEquals(2, resultado);
	}
}
