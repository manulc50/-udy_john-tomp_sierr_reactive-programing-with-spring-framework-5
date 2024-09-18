package guru.springframework.netfluxexample.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import guru.springframework.netfluxexample.domain.Movie;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String>{

}
