package guru.springframework.sfgrestbrewery.web.controllers;


import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.models.BeerDto;
import guru.springframework.sfgrestbrewery.web.models.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.models.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
	
	private static final Integer DEFAULT_PAGE_NUMBER = 0;
	private static final Integer DEFAULT_PAGE_SIZE = 25;
	
	private final BeerService beerService;
	
	@GetMapping
	public Mono<ResponseEntity<BeerPagedList>> listBeers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
												   @RequestParam(value = "pageSize", required = false) Integer pageSize,
												   @RequestParam(value = "beerName", required = false) String beerName,
												   @RequestParam(value = "beerStyle", required = false) BeerStyleEnum beerStyle,
												   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
	
		if(pageNumber == null || pageNumber < 0)
			pageNumber = DEFAULT_PAGE_NUMBER;
		
		if(pageSize == null || pageSize < 1)
			pageSize = DEFAULT_PAGE_SIZE;
		
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		return beerService.listBeers(beerName,beerStyle,showInventoryOnHand,PageRequest.of(pageNumber,pageSize))
				.map(beerPagedList -> new ResponseEntity<>(beerPagedList, HttpStatus.OK));
	}
	
	@GetMapping("/{beerId}")
	public Mono<ResponseEntity<BeerDto>> getBeerById(@PathVariable Integer beerId,@RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		return beerService.getBeerById(beerId,showInventoryOnHand)
				.map(ResponseEntity::ok) // Versión simplificada de la expresión "beerDto -> ResponseEntity.ok(beerDto)"
				.defaultIfEmpty(ResponseEntity.notFound().build());
				
	}
	
	@GetMapping("/{upc}/beerUpc")
	public Mono<ResponseEntity<BeerDto>> getBeerByUpc(@PathVariable(value = "upc") String beerUpc,@RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		return beerService.getBeerByUpc(beerUpc,showInventoryOnHand)
				.map(ResponseEntity::ok) // Versión simplificada de la expresión "beerDto -> ResponseEntity.ok(beerDto)"
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
	}

	@PostMapping
	public Mono<ResponseEntity<Void>> saveNewBeer(@Validated @RequestBody BeerDto beerDto) {
		
		return beerService.saveNewBeer(beerDto)
				.map(savedDto -> {
					HttpHeaders headers = new HttpHeaders();
					headers.add("Location","/api/v1/beer/" + savedDto.getId().toString());
					return headers;
				})
				.map(headers ->  new ResponseEntity<>(headers, HttpStatus.CREATED));
	}
	
	@PutMapping("/{beerId}")
	public Mono<ResponseEntity<Void>> updateBeerById(@PathVariable Integer beerId,@Validated @RequestBody BeerDto beerDto){
		return beerService.updateBeer(beerId, beerDto)
				.map(bDto -> bDto.getId() == null ? ResponseEntity.notFound().build()
						: ResponseEntity.noContent().build());

	}
	
	@DeleteMapping("/{beerId}")
	public Mono<ResponseEntity<Void>> deleteBeer(@PathVariable Integer beerId) {
		return beerService.deleteBeerById(beerId)
				.map(bDto -> bDto.getId() == null ? ResponseEntity.notFound().build()
						  : ResponseEntity.noContent().build());
			
	}
}
