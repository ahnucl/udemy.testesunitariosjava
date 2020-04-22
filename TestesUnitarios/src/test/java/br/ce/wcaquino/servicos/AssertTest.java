package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Ignore
	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);		
		
		Assert.assertEquals(0.51, 0.519, 0.01);
		Assert.assertEquals(Math.PI, 3.14, 0.0016);
		System.out.println(Math.PI);
		
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2); // Passando o tipo primitivo para objeto
		Assert.assertEquals(i, i2.intValue()); // Passando o objeto para tipo primitivo
		// Assert.assertEquals(i, i2); //Erro
		
		//Assert.assertEquals("bola", "Bola");	// Erro
		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("1");
		Usuario u2 = new Usuario("1");
		Usuario u3 = u1;
		
		Assert.assertEquals(u1, u2);
		Assert.assertEquals(u1, u3);	
		Assert.assertSame(u1, u3);
		
		/**
		 * Assertivas negativas:
		 * 	assertNotEquals
		 *  assertNotSame
		 *  assertNotNull
		 */
		
		/**
		 * cada assertiva aceita um parâmetro opcional
		 * é possível passar uma string de erro no primeiro parâmetro
		 * 
		 */
	}
}
