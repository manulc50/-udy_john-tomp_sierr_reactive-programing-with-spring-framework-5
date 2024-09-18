package guru.springframework.reactiveexamples;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import guru.springframework.reactiveexamples.commands.PersonCommand;
import guru.springframework.reactiveexamples.domain.Person;

/**
 * Created by jt on 8/24/17.
 */
@Slf4j
public class ReactiveExamplesTest {

	Person michael = Person.builder().id(1).firstName("Michael").lastName("Weston").build();
	Person fiona = Person.builder().id(2).firstName("Fiona").lastName("Glenanne").build();
	Person sam = Person.builder().id(3).firstName("Sam").lastName("Axe").build();
	Person jesse = Person.builder().id(4).firstName("Jesse").lastName("Porter").build();

    @Test
    public void monoTests() throws Exception {
        //create new person mono
        Mono<Person> personMono = Mono.just(michael);

        //get person object from mono publisher
        Person person = personMono.block();

        // output name
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform() throws Exception {
        //create new person mono
        Mono<Person> personMono = Mono.just(fiona);

        PersonCommand command = personMono
                .map(person -> { //type transformation
                    return new PersonCommand(person);
                }).block();

        log.info(command.sayMyName());
    }

    @Test()
    public void monoFilter() throws Exception {
    	// filter example
    	// Si el flujo Reactivo Flux resultante de aplicar el método "filter" está vacío, el método "single" emite una excepción de tipo NoSuchElementException
        Mono<Person> personMono = Mono.just(sam)
        		.filter(person -> person.getFirstName().equalsIgnoreCase("foo")).single();

        StepVerifier.create(personMono)
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    public void fluxTest() throws Exception {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.subscribe(person -> log.info(person.sayMyName()));

    }
    
    @Test
    public void fluxThenManyTest() throws Exception{
    	Flux<String> flux =Flux.just(1, 2, 3, 4)
    			.doOnNext(n -> System.out.println(n))
    			.map(n -> n * 2)
    			.doOnNext(n -> System.out.println(n))
    			// El método "thenMany" espera a que el flujo reactivo Flux original termine de emitir su último elemento para comenzar a procesar un nuevo flujo reactivo Flux, es decir, comienza a procesar un nuevo flujo reactivo Flux cuando se produce el evento OnComplete del flujo reactivo Flux original
    			// Al final, los elementos del flujo reactivo Flux original son ignorados y se tienen en cuenta los elementos del nuevo flujo reactivo Flux
    			.thenMany(Flux.just("michael", "fiona", "sam", "jesse"));
    	
    	flux.subscribe(nombre -> log.info(nombre));
    }
    
    @Test
    public void fluxThenTest() throws Exception{
    	Mono<String> mono = Flux.just(1, 2, 3, 4)
    			.doOnNext(n -> System.out.println(n))
    			.map(n -> n * 2)
    			.doOnNext(n -> System.out.println(n))
    			// El método "then" espera a que el flujo reactivo Flux original termine de emitir su último elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo Flux original
    			// Al final, los elementos del flujo reactivo Flux original son ignorados y se tienen en cuenta el elemento del nuevo flujo reactivo Mono
    			.then(Mono.just("michael"));
    	
    	mono.subscribe(nombre -> log.info(nombre));
    }
    
    @Test
    public void monoThenTest() throws Exception{
    	Mono<String> mono = Mono.just(1)
    			.doOnNext(n -> System.out.println(n))
    			.map(n -> n * 2)
    			.doOnNext(n -> System.out.println(n))
    			// El método "then" espera a que el flujo reactivo Mono original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Mono, es decir, comienza a procesar un nuevo flujo reactivo Mono cuando se produce el evento OnComplete del flujo reactivo Mono original
    			// Al final, el elemento del flujo reactivo Mono original es ignorado y se tienen en cuenta el elemento del nuevo flujo reactivo Mono
    			.then(Mono.just("michael"));
    	
    	mono.subscribe(nombre -> log.info(nombre));
    }
    
    @Test
    public void monoThenManyTest() throws Exception{
    	Flux<String> flux = Mono.just(1)
    			.doOnNext(n -> System.out.println(n))
    			.map(n -> n * 2)
    			.doOnNext(n -> System.out.println(n))
    			// El método "thenMany" espera a que el flujo reactivo Mono original termine de emitir su único elemento para comenzar a procesar un nuevo flujo reactivo Flux, es decir, comienza a procesar un nuevo flujo reactivo Flux cuando se produce el evento OnComplete del flujo reactivo Mono original
    			// Al final, el elemento del flujo reactivo Mono original es ignorado y se tienen en cuenta los elementos del nuevo flujo reactivo Flux
    			.thenMany(Flux.just("michael", "fiona", "sam", "jesse"));
    	
    	flux.subscribe(nombre -> log.info(nombre));
    }

    @Test
    public void fluxTestFilter() throws Exception {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.filter(person -> person.getFirstName().equals(fiona.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));

    }

    // Este test no llega a mostrar nada por consola porque, como se establece un retraso de 1 segundo en la emisión de cada elemento del flujo reactivo Flux y el precesamiento de cada elemento se realiza en segundo plano al tratarse de progrmación reactiva, la ejecución del test termina antes de procesarse estos elementos
    @Test
    public void fluxTestDelayNoOutput() throws Exception {

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));

    }

    // Este test si muetra el resultado por consola porque, aunque se ha establecido un retraso de 1 segundo para la emisión de cada elemento del flujo reactivo Flux, la sentencia "countDownLatch.await();" hace que el test espere a la finalización del contador antes de terminar su ejecución
    @Test
    public void fluxTestDelay() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();

    }

    // Este test si muetra el resultado por consola porque, aunque se ha establecido un retraso de 1 segundo para la emisión de cada elemento del flujo reactivo Flux, la sentencia "countDownLatch.await();" hace que el test espere a la finalización del contador antes de terminar su ejecución
    @Test
    public void fluxTestFilterDelay() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> people = Flux.just(michael, fiona, sam, jesse);

        people.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("i"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }

}
