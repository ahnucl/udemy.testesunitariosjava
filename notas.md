## Alguns componentes do XUnit
- TestRunner
- TestFixture
- TestSuites
- TestResultFormatter - para utilizar precisa usar as Assertivas (item abaixo)
- Assertions

+ Ver livro do Martin Fowler sobre xUnit

##

Testes devem ser:
- Fast
- Independent
- Repeatable
- Self-Verifying
- Timely (Tempo oportuno, retorno sobre investimento)


Tirar o método main faz o projeto não ser visto como uma aplicação java, mas a presença do Test faz o eclipse ver que é possível rodar pelo JUnit

Clicar no teste -> ou usar ctrl+F11 sobre tudo ou só sobre o teste (cursor sobre o NOME do teste)

Quando um teste encontra um erro ele para naquele ponto (outros testes são executados de forma independente)

Organização -> separar os métodos de teste da classe que estamos testando; no envio para produção, os testes nem serão enviados

Todo teste unitário tem um CENÁRIO, uma AÇÃO e uma VERIFICAÇÃO

Source folder -> Onde ficam os códigos
	se os pacotes dos source folders forem idênticos, o java considera tudo no mesmo pacote 

## Assertivas

Testes lógicos

assertEquals -> vários tipos, mas se comporta de forma diferente para float e double
	-> necessário inserir um terceiro parâmetro (um delta de comparação) -> Assert.assertEquals(0.51, 0.51, **0.01**);
	-> os extremos não são considerados iguais
	-> útil com dízimas periódicas ou outros números infinitos

## Wrappers

Tipos primitivos possuem um representação em objeto -> Wrapper, autoboxing e unboxing que variam entre o tipo primitivo e o wrapper automaticamente

```
	int i = 5;
	Integer i2 = 5;
	Assert.assertEquals(Integer.valueOf(i), i2); // Passando o tipo primitivo para objeto
	Assert.assertEquals(i, i2.intValue()); // Passando o objeto para tipo primitivo
	// Assert.assertEquals(i, i2); //Erro
```

Para String, Objetos-> usar métodos da própria classe - **"Quem deve dizer se um objeto é igual a outro é o próprio objeto"**
	-> implementar método equals do objeto

```
	//Assert.assertEquals("bola", "Bola");	// Erro		Assert.assertEquals("bola", "bola");
	Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
	Assert.assertTrue("bola".startsWith("bo"));
``` 

Garantir que dois objetos são a mesma instância -> assertSame
Se nulo -> assertTrue(objeto == null) ou assertNull(objeto)

Assertivas negativas:
 	assertNotEquals
	assertNotSame
  	assertNotNull

cada assertiva aceita um parâmetro opcional é possível passar uma string de erro no primeiro parâmetro

## AssertThat 
- Super genérico - biblioteca de assertivas para ser utilizada
- leitura fluída do método - **fluent interface** (Martin Fowler)

- O valor esperado é representado através de um "Matcher" (hamcrest já tem uns prontos)

## Hamcrest - API de Matchers

http://hamcrest.org/JavaHamcrest/javadoc/1.3/index.html?help-doc.html

Tutorial: http://hamcrest.org/JavaHamcrest/tutorial
-> Permite que regras de combinação ("match") sejam escritas **declarativamente** (paradigma declarativo)
-> match = "combinar"

## Separar testes

Muitas assertivas juntas -> um erro em qualquer delas para imediatamente o teste em questão

A separação torna os testes independentes -> se uma assertiva tiver erro antes de outra, a segunda fica dependendo da correção da primeira

## Rules do JUnit

https://junit.org/junit4/javadoc/4.12/org/junit/rules/TestRule.html

ErrorCollector -> permite trabalhar com um teste com várias assertivas (mesmo contexto) e caso uma falhe não trava as demais
	-> Não é bem a forma recomendada... (uma assertiva por teste) 

## Lançamento de exceções em testes 

Só usando o Assert.fail() perde o local exato do erro, seria necessário debugar

O Java obriga a dar um tratamento para a exceção - solução: lançar a exceção para quem 

Falha != erro -> Falhas ocorrem quando o teste é executado sem problema porém alguma condição esperada não foi atendida (assertiva); erro acontece quando algum problema durante a execução do teste impede que ele seja concluído -> ocorre exceção não experada e não tratadada

Caso o teste esteja esperando a exceção -> 
	elegante -> usar @Test(expected = Exception.class)
			 -> não consegue verificar a mensagem ; solução: criar exceções específicas para cada problema
			 -> garantir que cada exceção acontece por apenas um motivo
	robusta -> você tem todo o controle: usar try catch e criar uma assertiva com a mensagem de exceção
		problema: o teste é realizado "com sucesso" sempre que ele chega até o fim sem uma falha; cuidado para não gerar falso positivo!
	nova -> usa RULE 

```
	/**
	 * Forma elegante de testar esperando Exceções
	 * @throws Exception
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void testLocacao_filmeSemEstoque() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
	}
	
	/**
	 * Forma robusta de testar esperando Exceções
	 * @throws Exception
	 */
	@Test
	public void testLocacao_filmeSemEstoque_2() {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		//acao
		try {
			Locacao locacao = service.alugarFilme(usuario, filme);
			fail("Deveria ter lançado uma exceção");
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testLocacao_filmeSemEstoque_3() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		exception.expect(Exception.class); // essa expectativa é parte do cenário, deve ser declarada antes da ação
		exception.expectMessage("Filme sem estoque");
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);	
	}
```

Recapitulando:
Elegante -> funciona bem quando apenas a exceção importa, a mensagem não é necessária; ou seja, é possível garantir os casos em que cada exceção ocorre
Nova -> funciona na maioria dos casos em que a mensagem é necessária, mas não fornece controle após o lançamento da exceção
Robusta -> faz tudo, é mais completa

## Before e After

BeforeClass e AfterClass -> rodam apenas uma vez em toda a classe (antes da classe ser instanciada e após ela ser finalizada)
	-> Precisam ser estáticos pois o JUnit vai acessar esses métodos antes da classe ser criada!

O JUnit reinicializar as variáveis da classe para garantir a independência dos testes, como se cada teste trabalhasse com uma nova instância da classe -> mudar o campo para static: isso retira ele do escopo da isntância do teste, ela passa para o escopo da classe, esse tipo de variável o junit não vai reinicializar

Passar variáveis entre testes -> mesma coisa, usar variáveis estáticas. O JUnit não irá reinicializá-las (mas lembrando que, a não ser que seja especificado, o **junit não garante que os testes são executados na sequência em que são declarados**)

## Ordem de execução dos testes

Garantir a ordem: métodos deixam de ser testes e é criado um outro método que chama os anteriores
	-> essa forma perde a reastreabilidade do teste e cria um escopo diferente para a execução 

Anotação @FixMethodOrder(MethodSorted.XXXX)
	-> Testes em ordem alfabética; não é muito recomendado

Possível ordenar por outros critérios, inclusive especificar na mão qual ordem


Se o CENÁRIO e a AÇÃO forem os mesmos, é possível juntar várias VERIFICAÇÕES (asserrtivas)

## TDD, parte 3

Gerou testes conflitantes, gerando sempre uma bateria de testes no vermelho, ficou de resolver isso à frente

## @Ignore e Assumptions

Simplesmente tirar o @Test deixa o código morto. 

O "Assume" funciona como um "Ignore" condicional -> quando a solução final for apresentada, ambos devem ser retirados

## Testes parametrizáveis

"Data Driven Test"

mudar o Runner do junit para Parameterized
metódos e variáveis de parâmetro precisam ser estáticas
Para cada índice da massa de dados cada teste é executado dentro da classe com o runner parametrized

na anotação @Parameters -> {index} pega o índice do teste, e {0}, {1}, etc pegam os valores dos parâmetros para aquele teste

Estrutura bem complexa mas muito dinâmica! - usado bastante no dia a dia

## Matchers próprios

Melhorar a legibilidade do código ou centralizar alguma regra específica do projeto
Padrão de projeto Decorator - o método 

## Suíte de teste

Executar bateria de testes juntas

1. Pacote específico para a suíte
2. alterar Runner (@RunWith(Suite.class))
3. listar classes que compoem a suite com @SuiteClasses({})

É possível usar o @Before e @After na classe de Suite

Em testes funcionais seria possível prepara um banco de dados, por exemplo.

Problemas com suites: 
	1. cada nova classe criada precisa ser manualmente inserida na suite
	2. em ferramentas de integração contínua pode gerar duplicidade de execução de testes (conforme acontece com o eclipse)

## Criação de dados para testes

Padrão DataBuilder + Fluent Interface

-> COntrutur da classe builder privado -> fluência da leitura (não usar o construtor diretamente)

Usar os próprios testes pra aplicar TDD às classes de builder

Padrão Chain Method -> permite chamar vários métodos que retornam a própria classe builder até que o último método é chamado - este retorna a classe em si que o builder é responsável por construir

Não criar todos os métodos para alterar valores no momento da criação do builder; cria-los sob demanda

Padrão "Object Mother" (?) -> vários pontos de entrada com cenários específicos

Lib do professor -> Builder Master (github: https://github.com/wcaquino/BuilderMaster)
Ela cria o código que dos builders

## Análise de Cobertura 

No Eclipse é necessário instalar um plugin para analisar a cobertura -> EclEmma

Branches

100% de cobertura não indica que um código está 100% testado nem 100% livre de erros

Cobertura de teste não deve ser usada para medir a qualidade do código, mas para ver se existe algum ponto que não está sendo coberto por algum teste.

## Dependências externas

Se implementar alguma camada de dependência o teste deixa de ser unitário para ser de integração

Um teste unitário não deve ter dependências externas, como acesso a banco, rede, arquivos e qualquer entidade externa ao código que desejamos testar.
	-> princípio FIRST
	- Fast: acesso a banco, arquivos, rede, consome alguns segunos
	- Independent: testes não ficarão isolados- instabilidade de rede, banco não configuradao, massa de dados necessária para determinado cenários
	- Repeatable: ambiente e banco sempre nas mesmas condições e com os dados disponíveis
	- Timely: momento oportuno para criação dos testes terá passado pois é necessário configurar banco, etc

"Shift left" - criar testes o mais cedo possível visando evitar problemas
	-> em testes integrados > virtualizar os serviços ainda não disponíveis
	-> em testes unitários > TDD + Mocks

## Objetos Falsos

Solução 1: não será final mas fornece o mesmo resultado -> "Fake Object"
	Necessário implemetar uma classe sempre que precisar isolar testes de uma entidade externa
Solução 2: solução real (próximo vídeo)

## Mockito

Objeto Mock vs Objeto Fake -> mocks podem ter comportamentos dinâmicos e verificar resultados

## Gravando expectativas

Cabe ao desenvolvedor "ensinar" ao Mock o comportamento

O retorno padrão de um Mock é "false"

Cuidado pra não aplicar o verify em todas as interações

## Verificando Comportamentos

Exemplo:
```
	when(dao.obterLocacoesPendentes()).thenReturn(locacoesPendentes);
					
	//acao
	service.notificarAtrasos();
	
	//verificacao
	verify(email).notificarAtrasao(usuario);
```

## 30 - Verificando Comportamentos 2

Com o Mockito.verify() é possível verificar se algo foi executado ou se nunca foi executado!

Mockito.verifyNoMoreInteractions(Mock mock) -> garante que não houveram mais interações no mock especificado

Mockito.verifyZeroInteractions(mock) -> verifica que nenhuma interação ocorreu no mock especificado

PERMANECER NO ESCOPO DO CENÁRIO -> o método de notificar atrasos não trabalha com o spc, não verificar esse mock

Verificar chamada de método mais de uma vez -> Mockito.verify(mock, Mockito.times(int))

Verificar chamada de método pelo menos uma vez -> Mockito.verify(mock, MOckito.atLeastOnce())

Matcher do Mockito é diferente do que vimos no hamcrest -> aqui é como um "coringa"
	Se o método em teste possuir mais de um parâmetro e se for usado matcher em um parâmetros, deve-se usar matcher nos demais
	Existem métodos "anyTipo" para cada tipo concreto, e um Mockito.eq() para especificar um valor para o Matcher

Verificação genérica -> verify(email, times(3)).notificarAtrasao(Mockito.any(Usuario.class));

Comportamentos padrão do MOCK -> retornar valor default para aquele tipo de retorno (null, zero, etc)

## Anotações

Anotações @Mock e @InjectMocks e método MockitoAnnotations.initMocks(this) permitem remover os métodos de set das dependências (reflexão?)

## Lançando Exceções

when().thenThrows() -> verificar se a exceção está sendo tratada corretamente

## Capturando Argumentos

Classe ArgumentCaptor

argCapt.capture() -> quer dizer "quando esse método for chamado, capture o valor desse parâmetro e coloque aqui!"

usar argCapt.capture() no lugar de matcher (do mockito) pode ser útil porque é possível verificar tais valores depois! (num verify por exemplo)
	-> CUIDADO: argCapt.capture() deixa genérico! 

## Spy	

funciona parecido com o @Mock mas ao contrário

Quando a entidade não sabe o que fazer: o mock retorna o valor padrão, o spy retorna o valor do método (real execução)

Spy não funciona com interfaces, apenas com classes concretas

É possível fazer o Mock retornar o método real -> ".thenCallRealMethod()"

método void -> no spy o padrão é executar o método

## PowerMock

Extende outras ferramentas de mock

Operações que o mockito não trabalha -> mockar o construtor de um objeto, alterar o comportamento de um método estático ou privado

## Mockando construtores

PowerMockito.whenNew(Date.class).withNoArguments()

## Mockando métodos privados 

assinatura muda, passar nomes dos métodos como Strings

## Testando métodos privados diretamente

Classe whitebox -> tem no mockito e no powermock, mas só a do powermock consegue testar métodos privados

## The dark side of powermock

Trocar métodos privados por métodos protegidos -> teste fácil no mesmo pacote

Outra forma de reestruturar com uma classe de serviço para Date

Invocar métodos privados -> API própria do java: Reflection

getMethod retorna apenas métodos visíveis / getDeclaredMethods retorna todos os métodos

## Testes em Paralelo, parte 1

em testes unitários é tranquilo, talvez dê problema em uso de dependências estáticas compartilhadas

Caso não se especifique um runner para o teste, ele usará -> @RunWith(Junit4.class)

Junit4 é um proxy pra versão mais atual

a partir da 4.5 o runner é BlockJUnit4ClassRunner

## 43 - Execução dos Testes via Maven

criar variável de ambiente MAVEN_HOME e adicionar ao PATH

"mvn test"

## 44 - Testes em paralelo, parte 2

Criar estrutura dentro do pom para usar o plugin "surefire" :

  <build>
  	<testSourceDirectory>src</testSourceDirectory>
  	<plugins>
  		<plugin>
  			<groupId>org.apache.maven.plugins</groupId>
  			<artifactId>maven-surefire-plugin</artifactId>
  			<version>2.18.1</version>
  			<configuration>
  				<threadCount>2</threadCount>
  				<parallel>all</parallel> <!-- Especifica o que se deseja paralelizar -->
  			</configuration>
  		</plugin>
  	</plugins>
  </build>

Remover os runners de paralelismo

-> StringBuffer é threadsafe (não corre risco de ter várias threads tentando escrever nele ao mesmo tempo)

Pre-requisito do surefire -> todos os runners devem estar na mesma hierarquia da classe ParentRunner

PowerMock não herda da ParentRunner

Nome sobre a classe 
	-> CTRL + O -> outline rápido
	-> CTRL + T -> hierarquia rápida
	(rápido quer dizer que não é aberta uma nova aba com a informação, apenas um menu "flutuante")

É possível fazer testes funcionais, automatizados, e de integração com JUnit

O  <useUnlimitedThreads>true</useUnlimitedThreads> desconsidera o 	<threadCount>5</threadCount> 

Executar via Maven a partir do eclipse

Pelo maven é possível rodar testes em paralelos de uma forma menos intrusiva (sem precisar editar classes de teste, etc)