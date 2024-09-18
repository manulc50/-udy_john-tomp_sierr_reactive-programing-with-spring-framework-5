package guru.springframework.netfluxexample.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.netfluxexample.domain.Movie;
import guru.springframework.netfluxexample.domain.MovieEvent;
import guru.springframework.netfluxexample.services.MovieService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
	
	private final MovieService movieService;
	
	// Como en este caso, el método "streamMovieEvents" de nuestro servicio "movieService" devuelve un flujo reactivo Flux que emite continuamente y cada segundo un evento de una película, cambiamos el MediatType de la respuesta de "JSON"(MediaType por defecto) a "TEXT_EVENT_STREAM_VALUE" porque estamos devolviendo un stream continuo de datos sin final
	@GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<MovieEvent> streamMovieEvents(@PathVariable(value = "id") String movieId){
		return movieService.streamMovieEvents(movieId);
	}
	
	@GetMapping
	Flux<Movie> getAllMovies(){
		return movieService.getAllMovies();
	}
	
	@GetMapping("/{id}")
	Mono<Movie> getMovieById(@PathVariable String id){
		return movieService.getMovieById(id);
	}

}
