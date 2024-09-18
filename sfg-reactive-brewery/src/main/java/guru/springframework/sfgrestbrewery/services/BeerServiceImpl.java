package guru.springframework.sfgrestbrewery.services;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import guru.springframework.sfgrestbrewery.domain.Beer;
import guru.springframework.sfgrestbrewery.repositories.BeerRepository;
import guru.springframework.sfgrestbrewery.web.mappers.BeerMapper;
import guru.springframework.sfgrestbrewery.web.models.BeerDto;
import guru.springframework.sfgrestbrewery.web.models.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.models.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Query.empty;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService{
	
	private final BeerRepository beerRepository ;
	private final BeerMapper beerMapper;
	private final R2dbcEntityTemplate template; // Para realizar consultas a la base de datos usando el driver reactivo R2DBC
	
	// Nota: Debido a que actualmente el driver reactivo R2DBC no soporta paginación, tenemos que realizar manualmente la consulta a la base de datos y crear nuestro objeto de paginación BeerPagedList a partir de los resultados obtenidos en esa consulta
	@Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
	@Override
	public Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, Boolean showInventoryOnHand, PageRequest pageRequest) {

		Query query = null;
		
		// Búsqueda por "beerName" y "beerStyle"
		if(StringUtils.hasText(beerName) && !ObjectUtils.isEmpty(beerStyle))
			query = query(where("beerName").is(beerName).and("beerStyle").is(beerStyle.name()));
		// Búsqueda por "beerName"
		else if(StringUtils.hasText(beerName) && ObjectUtils.isEmpty(beerStyle))
			query = query(where("beerName").is(beerName));
		// Búsqueda por "beerStyle"
		else if(!StringUtils.hasText(beerName) && !ObjectUtils.isEmpty(beerStyle))
			query = query(where("beerStyle").is(beerStyle));
		// Búsqueda sin filtros
		else
			query = empty();
		
		return template.select(Beer.class)
				.matching(query.with(pageRequest))
				.all()
				.map(beerMapper::beerToBeerDto) // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
				.collectList()
				.map(listBeers -> new BeerPagedList(listBeers, PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize()), listBeers.size()));
				
	}

	@Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false")
	@Override
	public Mono<BeerDto> getBeerById(Integer beerId, Boolean showInventoryOnHand) {
		return showInventoryOnHand ? beerRepository.findById(beerId)
										.map(beerMapper::beerToBeerDtoWithInventory) // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDtoWithInventory(beer)"
								   : beerRepository.findById(beerId)
								   		.map(beerMapper::beerToBeerDto); // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
	}
	
	@Cacheable(cacheNames = "beerUpcCache", key = "#beerUpc", condition = "#showInventoryOnHand == false")
	@Override
	public Mono<BeerDto> getBeerByUpc(String beerUpc, Boolean showInventoryOnHand) {
		return showInventoryOnHand ? beerRepository.findByUpc(beerUpc)
										.map(beerMapper::beerToBeerDtoWithInventory) // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDtoWithInventory(beer)"
								   : beerRepository.findByUpc(beerUpc)
								   		.map(beerMapper::beerToBeerDto); // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
	}

	@Override
	public Mono<BeerDto> saveNewBeer(BeerDto beerDto) {
		return beerRepository.save(beerMapper.beerDtoToBeer(beerDto))
				.map(beerMapper::beerToBeerDto); // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
	}

	@Override
	public Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto) {
		return beerRepository.findById(beerId)
				.map(beer -> {
					beer.setBeerName(beerDto.getBeerName());
					beer.setBeerStyle(beerDto.getBeerStyle());
					beer.setPrice(beerDto.getPrice());
					beer.setUpc(beer.getUpc());
					
					return beer;
				})
				.flatMap(beerRepository::save) // Versión simplificada de la expresión "beer -> beerRepository.save(beer)"
				.map(beerMapper::beerToBeerDto) // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
				.defaultIfEmpty(BeerDto.builder().build());
	}

	@Override
	public Mono<BeerDto> deleteBeerById(Integer beerId) {
		return beerRepository.findById(beerId)
				.flatMap(beer -> beerRepository.delete(beer).thenReturn(beer))
				.map(beerMapper::beerToBeerDto) // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
				.defaultIfEmpty(BeerDto.builder().build());
	}

}
