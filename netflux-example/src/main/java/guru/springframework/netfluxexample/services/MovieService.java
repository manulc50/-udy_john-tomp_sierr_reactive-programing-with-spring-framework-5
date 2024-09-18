package guru.springframework.netfluxexample.services;

import guru.springframework.netfluxexample.domain.Movie;
import guru.springframework.netfluxexample.domain.MovieEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

	Flux<MovieEvent> streamMovieEvents(String movieId);
	Flux<Movie> getAllMovies();
	Mono<Movie> getMovieById(String id);
	
}
