package guru.springframework.reactivebeerclient.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import guru.springframework.reactivebeerclient.config.WebClientProperties;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {
	
	private final WebClient webClient;
	
	@Override
	public Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle,
			Boolean showInventoryOnHand) {

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_V1_PATH)
						// Son parámetros opcionales
						.queryParamIfPresent("pageNumber", Optional.ofNullable(pageNumber))
						.queryParamIfPresent("pageSize", Optional.ofNullable(pageSize))
						.queryParamIfPresent("beerName", Optional.ofNullable(beerName))
						.queryParamIfPresent("beerStyle", Optional.ofNullable(beerStyle))
						.queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
						.build()
				)
				.retrieve()
				.bodyToMono(BeerPagedList.class);
	}

	@Override
	public Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_V1_GET_BY_ID_PATH)
						// El parámetro es opcional
						.queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
						.build(id)
				)
				.retrieve()
				.bodyToMono(BeerDto.class);
	}

	@Override
	public Mono<BeerDto> getBeerByUpc(String upc) {
		return webClient.get()
				.uri(WebClientProperties.BEER_V1_GET_BY_UPC_PATH, upc)
				.retrieve()
				.bodyToMono(BeerDto.class);
	}

	@Override
	public Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto) {
		return webClient.post()
				.uri(WebClientProperties.BEER_V1_PATH)
				.bodyValue(beerDto)
				.retrieve()
				.toBodilessEntity();
	}

	@Override
	public Mono<ResponseEntity<Void>> updateBeer(UUID id, BeerDto beerDto) {
		return webClient.put()
				.uri(WebClientProperties.BEER_V1_GET_BY_ID_PATH, id)
				.body(BodyInserters.fromValue(beerDto))
				.retrieve()
				.toBodilessEntity();
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteBeer(UUID id) {
		return webClient.delete()
				.uri(WebClientProperties.BEER_V1_GET_BY_ID_PATH, id)
				.retrieve()
				.toBodilessEntity();
	}

}
