package guru.springframework.netfluxexample.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import guru.springframework.netfluxexample.domain.Movie;
import guru.springframework.netfluxexample.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class InitMovies implements CommandLineRunner{
	
	private final MovieRepository movieRepository;

	@Override
	public void run(String... args) throws Exception {
		// Clear all data
		movieRepository.deleteAll()
			.thenMany(
				// Tenemos un flujo reactivo Flux de Strings
				Flux.just("Silence of the Lambdas", "AEon Flux", "Enter the Mono<Void>", "The Fluxxinator",
		                "Back to the Future", "Meet the Fluxes", "Lord of the Fluxes")
					// Convertimos el flujo reativo Flux de String en un flujo reactivo Flux de Movies
					.map(Movie::new) // Versión simplificada de la expresión "title -> new Movie(title)"
					// Como el método "save" del repositorio "movieRepository" nos devuelve un flujo reactivo Mono de tipo Movie por cada película persistida en la base de datos, usando "map" tendríamos un flujo reactivo Flux con flujos reactivos Mono de tipo Movie como elementos y, por esta razón, usamos "flatMap" para aplanar ese Flux de flujos reactivos Mono de tipo Movie en un Flux de elementos tipo Movie
					.flatMap(movieRepository::save) // Versión simplificada de la expresión "movie -> movieRepository.save(movie)"
					
			)
			// El primer parámetro de entrada del método "subscribe" se corresponde con la implementación de una tarea que se ejecutará para cada elemento emitido por el flujo reactivo al que estamos suscritos
			// El segundo argumento de entrada del método "subscribe" se trata de la implementación de una determinada tarea que se ejecutará cuando el flujo reactivo al que estamos suscritos emita o lance un error o excepción. Es decir, este argumento de entrada es para capturar errores o excepciones emitidos por el flujo reactivo al que estamos suscritos
			// El tercer parámetro de entrada del método "subscribe" se corresponde con el evento "onComplete" y nos permite implementar una tarea que se ejecutará justo después de emitirse el último elemento del flujo reactivo al que estamos suscritos
			.subscribe(null,null,() -> {
				movieRepository.findAll().subscribe(System.out::println); // Versión simplificada de la expresión "movie -> System.out.println(movie)"
			});
		
	}

}
