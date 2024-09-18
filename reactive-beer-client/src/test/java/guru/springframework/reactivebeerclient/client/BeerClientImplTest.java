package guru.springframework.reactivebeerclient.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import guru.springframework.reactivebeerclient.config.WebClientConfig;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import guru.springframework.reactivebeerclient.model.BeerStyleEnum;
import reactor.core.publisher.Mono;

public class BeerClientImplTest {
	
	BeerClientImpl beerClient;
	
	@BeforeEach
	void setUp() {
		beerClient = new BeerClientImpl(new WebClientConfig().webClient());
	}
	
	@Test
	void listBeersTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		assertThat(pagedList).isNotNull();
		assertThat(pagedList.getContent().size()).isGreaterThan(0);
		
		System.out.println(pagedList.toList());
	}
	
	@Test
	void listBeersPageSize10Test() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 10, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		assertThat(pagedList).isNotNull();
		assertThat(pagedList.getContent().size()).isEqualTo(10);
	}
	
	@Test
	void listBeersNoRecordsTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(10, 22, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		assertThat(pagedList).isNotNull();
		assertThat(pagedList.getContent().size()).isEqualTo(0);
	}
	
	@Disabled("API returning inventory when should not be")
	@Test
	void getBeerByIdTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		UUID beerId = pagedList.getContent().get(0).getId();
		
		Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, false);
		
		BeerDto beerDto = beerDtoMono.block();
		
		assertThat(beerDto).isNotNull();
		assertThat(beerDto.getId()).isEqualTo(beerId);
		assertThat(beerDto.getQuantityOnHand()).isNull();
	}
	
	@Test
	void getBeerByIdShowInventoryTrueTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		UUID beerId = pagedList.getContent().get(0).getId();
		
		Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, true);
		
		BeerDto beerDto = beerDtoMono.block();
		
		assertThat(beerDto).isNotNull();
		assertThat(beerDto.getId()).isEqualTo(beerId);
		assertThat(beerDto.getQuantityOnHand()).isNotNull();
	}
	
	// Como la implementación de este test es todo reactivo, la ejecución del test termina antes de procesarse este flujo reactivo y no da tiempo a que se validen los "asserts"
	// Por esta razón, usamos la clase CountDownLatch que nos permite realizar tests de aplicaciones concurrentes, es decir, tests de entornos multihilo
	@Test
	void getBeerByIdFunctionalTest() throws InterruptedException {
		AtomicReference<BeerDto> beer = new AtomicReference<>();
		AtomicReference<UUID> uuid = new AtomicReference<>();
		CountDownLatch countDownLatch = new CountDownLatch(1); // Ponemos el valor del contador a 1 y, cuando se procese todo el flujo reactivo, lo actualizamos a 0 para que se verifiquen los "asserts" y se termine la ejecución de este test
		
		beerClient.listBeers(null, null, null, null, null)
			.map(beerPagedList -> beerPagedList.getContent().get(0).getId())
			.flatMap(beerId -> {
				uuid.set(beerId); // Para verificarlo en el hilo principal del test
				return beerClient.getBeerById(beerId, false);
			})
			.subscribe(beerDto -> {
				System.out.println(beerDto);
				beer.set(beerDto); // Para verificarlo en el hilo principal del test
				// Si ponemos los "asserts" aquí, estos "asserts" se verifican en el hilo o pipeline del flujo reactivo pero no en el hilo principal del test. Por esta razón, el test no sabe cuál ha sido el resultado de esos "asserts" 
				//assertThat(beerDto).isNotNull();
				//assertThat(beerDto.getId()).isEqualTo(uuid.get());
				countDownLatch.countDown(); // Actualizamos el valor del contador a 0 porque en este momento ya ha finalizado el procesamiento del flujo reactivo
			});
		
		countDownLatch.await(); // Se queda esperando hasta que el valor del contador sea 0
		
		assertThat(beer.get()).isNotNull();
		assertThat(beer.get().getId()).isEqualTo(uuid.get());
	}
	
	@Test
	void getBeerByUpcTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
	
		BeerPagedList pagedList = beerPagedListMono.block();
		
		String beerUpc = pagedList.getContent().get(0).getUpc();
		
		Mono<BeerDto> beerDtoMono = beerClient.getBeerByUpc(beerUpc);
		
		BeerDto beerDto = beerDtoMono.block();
		
		assertThat(beerDto).isNotNull();
		assertThat(beerDto.getUpc()).isEqualTo(beerUpc);
	}
	
	@Test
	void createBeerTest() {
		BeerDto beerDto = BeerDto.builder()
				.beerName("Dogfishhead 90 Min IPA")
				.beerStyle(BeerStyleEnum.IPA)
				.upc("234848549559")
				.price(new BigDecimal("10.99"))
				.build();
		
		Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
		
		ResponseEntity<Void> responseEntity = responseEntityMono.block();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	void updateBeerTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
		
		BeerPagedList pagedList = beerPagedListMono.block();
		
		BeerDto beerDto = pagedList.getContent().get(0);
		
		BeerDto updatedBeer = BeerDto.builder()
				.beerName("Really Good Beer")
				.beerStyle(beerDto.getBeerStyle())
				.price(beerDto.getPrice())
				.upc(beerDto.getUpc())
				.build();
		
		Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(), updatedBeer);
	
		ResponseEntity<Void> responseEntity = responseEntityMono.block();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	void deleteBeerTest() {
		Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
		
		BeerPagedList pagedList = beerPagedListMono.block();
		
		BeerDto beerDto = pagedList.getContent().get(0);
		
		Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(beerDto.getId());
		
		ResponseEntity<Void> responseEntity = responseEntityMono.block();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	void deleteBeerNotFoundTest() {
		Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());
		
		assertThrows(WebClientResponseException.class, () -> {
			ResponseEntity<Void> responseEntity = responseEntityMono.block(); // Lanza una excepción de tipo WebClientResponseException 
			assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		});
		
	}

	@Test
	void deleteBeerHandleExceptionTest() {
		Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());
		
		ResponseEntity<Void> responseEntity = responseEntityMono
				.onErrorResume(throwable -> {
					if(throwable instanceof WebClientResponseException) {
						WebClientResponseException exception = (WebClientResponseException) throwable;
						return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
					}
					else
						throw new RuntimeException(throwable);
				}).block();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
	}

}
