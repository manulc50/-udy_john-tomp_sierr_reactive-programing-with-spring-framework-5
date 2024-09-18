package guru.springframework.sfgrestbrewery.services;


import org.springframework.data.domain.PageRequest;

import guru.springframework.sfgrestbrewery.web.models.BeerDto;
import guru.springframework.sfgrestbrewery.web.models.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.models.BeerStyleEnum;
import reactor.core.publisher.Mono;

public interface BeerService {
	public Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, Boolean showInventoryOnHand, PageRequest pageRequest);
	public Mono<BeerDto> getBeerById(Integer beerId, Boolean showInventoryOnHand);
	public Mono<BeerDto> getBeerByUpc(String beerUpc, Boolean showInventoryOnHand);
	public Mono<BeerDto> saveNewBeer(BeerDto beerDto);
	public Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto);
	public Mono<BeerDto> deleteBeerById(Integer beerId);

}
