package guru.springframework.reactiveexamples.repositories;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guru.springframework.reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PersonRepositoryImplTest {
	
	private PersonRepositoryImpl personRepository;
	
	@BeforeEach
	void setUp() {
		personRepository = new PersonRepositoryImpl();
	}
	
	@Test
	void getByIdBlockTest() {
		Mono<Person> personMono = personRepository.getById(1);
		
		Person person = personMono.block();
		
		System.out.println(person);
	}

	@Test
	void getByIdSubscribeTest() {
		Mono<Person> personMono = personRepository.getById(1);
		
		StepVerifier.create(personMono)
		            .expectNextCount(1)
		            .verifyComplete();
		
		personMono.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void getByIdNotFoundSubscribeTest() {
		Mono<Person> personMono = personRepository.getById(8);
		
		StepVerifier.create(personMono)
        			.expectNextCount(0)
        			.verifyComplete();
		
		personMono.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void getByIdMapFunctionTest() {
		Mono<Person> personMono = personRepository.getById(1);
		
		personMono
			.doOnNext(System.out::println) // Versión simplificada de la expresión "person -> System.out.println(person)"
			.map(Person::getFirstName) // Versión simplificada de la expresión "person -> person.getFirstName()"
			.subscribe(System.out::println);  // Versión simplificada de la expresión "firstName -> System.out.println(firstName)"
	}
	
	@Test
	void fluxBlockFirstTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		Person person = personFlux.blockFirst();
		
		System.out.println(person);
	}
	
	@Test
	void fluxSubscribeTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		StepVerifier.create(personFlux)
					.expectNextCount(4)
					.verifyComplete();
		
		personFlux.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void fluxtoListMonoTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		Mono<List<Person>> personListMono = personFlux.collectList();
		
		personListMono.subscribe(list -> list.forEach(System.out::println)); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void findPersonByIdTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		final Integer id = 3;
		
		Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
		
		personMono.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void findPersonByIdNotFoundTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		final Integer id = 8;
		
		// Si el flujo Reactivo Flux resultante de aplicar el método "filter" está vacío, el método "next" emite un flujo reactivo Mono vacío
		Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
		
		personMono.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
	
	@Test
	void findPersonByIdNotFoundWithExceptionTest() {
		Flux<Person> personFlux = personRepository.findAll();
		
		final Integer id = 8;
		
		// Si el flujo Reactivo Flux resultante de aplicar el método "filter" está vacío, el método "single" emite una excepción de tipo NoSuchElementException 
		Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();
		
		personMono
			.doOnError(throwable -> System.out.println("I went boom"))
			.onErrorReturn(Person.builder().id(id).build())
			.subscribe(System.out::println); // Versión simplificada de la expresión "person -> System.out.println(person)"
	}
}
