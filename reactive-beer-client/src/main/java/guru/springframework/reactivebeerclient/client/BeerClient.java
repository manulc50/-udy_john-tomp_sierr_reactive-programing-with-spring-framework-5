package guru.springframework.reactivebeerclient.client;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import reactor.core.publisher.Mono;

public interface BeerClient {
	
	Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand);
	Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);
	Mono<BeerDto> getBeerByUpc(String upc);
	Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto);
	Mono<ResponseEntity<Void>> updateBeer(UUID id, BeerDto beerDto);
	Mono<ResponseEntity<Void>> deleteBeer(UUID id);
}
