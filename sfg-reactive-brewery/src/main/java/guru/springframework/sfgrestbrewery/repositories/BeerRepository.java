package guru.springframework.sfgrestbrewery.repositories;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import guru.springframework.sfgrestbrewery.domain.Beer;
import reactor.core.publisher.Mono;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer>{
	
	// Actualmente, el driver reactivo R2DBC no soporta paginación
	
	Mono<Beer> findByUpc(String upc);
}
