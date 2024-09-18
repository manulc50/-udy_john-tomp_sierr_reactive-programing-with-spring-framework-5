package guru.springframework.netfluxexample.services;

import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import guru.springframework.netfluxexample.domain.Movie;
import guru.springframework.netfluxexample.domain.MovieEvent;
import guru.springframework.netfluxexample.repositories.MovieRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieServiceImpl implements MovieService {
	
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public Flux<MovieEvent> streamMovieEvents(String movieId) {
		// Creamos y devolvemos un flujo reactivo Flux que emite continuamente y cada segundo un elemento de tipo "MovieEvent" que se corresponde con un evento de una película
		return Flux.<MovieEvent>generate(movieEventSynchronousSink ->
			movieEventSynchronousSink.next(new MovieEvent(movieId, new Date())))
				.delayElements(Duration.ofSeconds(1));
	}

	@Override
	public Flux<Movie> getAllMovies() {
		return movieRepository.findAll();
	}

	@Override
	public Mono<Movie> getMovieById(String id) {
		return movieRepository.findById(id);
	}

}
