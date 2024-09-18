package guru.springframework.streamingstockquoteservice.service;

import java.time.Duration;

import guru.springframework.streamingstockquoteservice.model.Quote;
import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {
	
	public Flux<Quote> fetchQuoteStream(Duration period);

}
