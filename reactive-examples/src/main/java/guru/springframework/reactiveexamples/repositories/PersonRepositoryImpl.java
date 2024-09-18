package guru.springframework.reactiveexamples.repositories;

import guru.springframework.reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {
	
	private Person michael = Person.builder().id(1).firstName("Michael").lastName("Weston").build();
	private Person fiona = Person.builder().id(2).firstName("Fiona").lastName("Glenanne").build();
	private Person sam = Person.builder().id(3).firstName("Sam").lastName("Axe").build();
	private Person jesse = Person.builder().id(4).firstName("Jesse").lastName("Porter").build();

	@Override
	public Mono<Person> getById(final Integer id) {
		// Si el flujo Reactivo Flux resultante de aplicar el método "filter" está vacío, el método "next" emite un flujo reactivo Mono vacío
		return findAll().filter(person -> person.getId() == id).next();
	}

	@Override
	public Flux<Person> findAll() {
		return Flux.just(michael, fiona, sam, jesse);
	}

}
