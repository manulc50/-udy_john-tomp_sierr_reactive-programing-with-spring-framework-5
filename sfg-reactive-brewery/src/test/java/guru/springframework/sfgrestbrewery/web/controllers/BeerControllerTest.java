package guru.springframework.sfgrestbrewery.web.controllers;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.models.BeerDto;
import guru.springframework.sfgrestbrewery.web.models.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.models.BeerStyleEnum;
import reactor.core.publisher.Mono;

@WebFluxTest(BeerController.class)
public class BeerControllerTest {
	
	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	BeerService beerService;
	
	List<BeerDto> validBeersList;
	
	@BeforeEach
	void setUp() {
		BeerDto validBeer1 = BeerDto.builder()
				.beerName("Test beer1")
				.beerStyle(BeerStyleEnum.PALE_ALE)
				.upc(BeerLoader.BEER_1_UPC)
				.build();
		
		BeerDto validBeer2 = BeerDto.builder()
				.beerName("Test beer2")
				.beerStyle(BeerStyleEnum.ALE)
				.upc(BeerLoader.BEER_2_UPC)
				.build();
		
		BeerDto validBeer3 = BeerDto.builder()
				.beerName("Test beer3")
				.beerStyle(BeerStyleEnum.IPA)
				.upc(BeerLoader.BEER_3_UPC)
				.build();
		
		validBeersList = Arrays.asList(validBeer1, validBeer2, validBeer3);
	}
	
	@Test
	void getBeerByIdTest() {
		Integer beerId = 1;
		BeerDto validBeer = validBeersList.get(0);
		
		given(beerService.getBeerById(any(), any())).willReturn(Mono.just(validBeer));
		
		webTestClient.get()
			.uri("/api/v1/beer/{beerId}", beerId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(BeerDto.class)
			.value(beerDto -> beerDto.getBeerName(), equalTo(validBeer.getBeerName()));
	}
	
	@Test
	void getBeerByUpcTest() {
		String beerUpc = BeerLoader.BEER_2_UPC;
		BeerDto validBeer = validBeersList.get(1);
		
		given(beerService.getBeerByUpc(any(), any())).willReturn(Mono.just(validBeer));
		
		webTestClient.get()
			.uri("/api/v1/beer/{beerUpc}/beerUpc", beerUpc)
			.exchange()
			.expectStatus().isOk()
			.expectBody(BeerDto.class)
			.value(beerDto -> beerDto.getBeerName(), equalTo(validBeer.getBeerName()));
	}
	
	@Test
	void listBeersTest() {
		BeerPagedList validPagedList = new BeerPagedList(validBeersList);
		
		given(beerService.listBeers(any(), any(), any(), any())).willReturn(Mono.just(validPagedList));
		
		webTestClient.get()
			.uri("/api/v1/beer")
			.exchange()
			.expectStatus().isOk()
			.expectBody(BeerPagedList.class)
			.value(beerPagedList -> beerPagedList.getContent().size(), equalTo(validBeersList.size()));
	}
}
