package guru.springframework.sfgrestbrewery.web.controllers;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.web.models.BeerDto;
import guru.springframework.sfgrestbrewery.web.models.BeerPagedList;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientIT {
	
	public static final String BASE_URL = "http://localhost:8080";

    WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                // Para que se muestre por consola los logs de las peticiones http que realiza este cliente
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                		.wiretap(true))) // Usa el formato de salida por defecto de los logs
                .build();
    }
    
    // Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
  	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
    @Test
    void listBeersTest() throws InterruptedException {
    	AtomicReference<BeerPagedList> pagedList = new AtomicReference<>();

        CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test

        Mono<BeerPagedList> beerPagedListMono = webClient.get()
        		.uri("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerPagedList.class);
        
        beerPagedListMono.subscribe(beerPagedList -> {
        	pagedList.set(beerPagedList); // Para verificarlo en el hilo principal del test
        	// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
        	//assertThat(beerPagedList).isNotNull();
            //assertThat(beerPagedList.getSize()).isEqualTo(25);

            beerPagedList.getContent().forEach(System.out::println); // Versión simplificada de la expresión "beerDto -> System.out.println(beerDto)"

            countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
        });

        countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
        
        assertThat(pagedList.get()).isNotNull();
        assertThat(pagedList.get().getSize()).isEqualTo(25);
    }

    // Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
   	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
    @Test
    void listBeersPageSize5Test() throws InterruptedException {
    	AtomicReference<BeerPagedList> pagedList = new AtomicReference<>();
    	Integer pageSize = 5;
    	
        CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test

        Mono<BeerPagedList> beerPagedListMono = webClient.get().uri(uriBuilder -> {
            return uriBuilder.path("/api/v1/beer")
            		.queryParam("pageSize", pageSize)
            		.build();
        		})
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerPagedList.class);

        beerPagedListMono.subscribe(beerPagedList -> {
        	pagedList.set(beerPagedList); // Para verificarlo en el hilo principal del test
        	// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
        	//assertThat(beerPagedList).isNotNull();
            //assertThat(beerPagedList.getSize()).isEqualTo(pageSize);

            beerPagedList.getContent().forEach(System.out::println); // Versión simplificada de la expresión "beerDto -> System.out.println(beerDto)"

            countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
        });

        countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
        
        assertThat(pagedList.get()).isNotNull();
        assertThat(pagedList.get().getSize()).isEqualTo(pageSize);
    }

    // Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
   	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
    @Test
    void lististBeersByNameTest() throws InterruptedException {
    	AtomicReference<BeerPagedList> pagedList = new AtomicReference<>();
    	String beerName = "Mango Bobs";
    	
        CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test

        Mono<BeerPagedList> beerPagedListMono = webClient.get().uri(uriBuilder -> {
            return uriBuilder.path("/api/v1/beer")
            		.queryParam("beerName", beerName)
            		.build();
        		})
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerPagedList.class);

        beerPagedListMono.subscribe(beerPagedList -> {
        	pagedList.set(beerPagedList); // Para verificarlo en el hilo principal del test
        	// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
        	//assertThat(beerPagedList).isNotNull();
            //assertThat(beerPagedList.getContent().size()).isEqualTo(1);
            //assertThat(beerPagedList.getContent().get(0).getBeerName()).isEqualTo(beerName);

            beerPagedList.getContent().forEach(System.out::println); // Versión simplificada de la expresión "beerDto -> System.out.println(beerDto)"

            countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
        });

        countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
 
        assertThat(pagedList.get()).isNotNull();
        assertThat(pagedList.get().getContent().size()).isEqualTo(1);
        assertThat(pagedList.get().getContent().get(0).getBeerName()).isEqualTo(beerName);
    }
    
    // Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
 	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
    @Test
    void getBeerByIdTest() throws InterruptedException {
    	AtomicReference<BeerDto> beer = new AtomicReference<>();
		Integer id = 1;
		
        CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test

        Mono<BeerDto> beerDtoMono = webClient.get()
        		.uri("api/v1/beer/{beerId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beerDto -> {
        	beer.set(beerDto); // Para verificarlo en el hilo principal del test
        	// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
            //assertThat(beerDto).isNotNull();
            //assertThat(beerDto.getBeerName()).isNotNull();

            countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
        });

        countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
        
        assertThat(beer.get()).isNotNull();
        assertThat(beer.get().getId()).isEqualTo(id);
        assertThat(beer.get().getBeerName()).isNotNull();
    }
    
    // Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
  	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
    @Test
    void getBeerByUpcTest() throws InterruptedException {
    	AtomicReference<BeerDto> beer = new AtomicReference<>();
    	String upc = BeerLoader.BEER_2_UPC;
    	
        CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test

        Mono<BeerDto> beerDtoMono = webClient.get()
        		.uri("api/v1/beer/{beerUpc}/beerUpc", upc)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beerDto -> {
        	beer.set(beerDto); // Para verificarlo en el hilo principal del test
        	// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
            //assertThat(beer).isNotNull();
            //assertThat(beer.getBeerName()).isNotNull();

            countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
        });

        countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
        
        assertThat(beer.get()).isNotNull();
        assertThat(beer.get().getUpc()).isEqualTo(upc);
        assertThat(beer.get().getBeerName()).isNotNull();
        
    }

}
