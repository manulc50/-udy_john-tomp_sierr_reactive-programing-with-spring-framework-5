package guru.springframework.streamingstockquoteservice.service.web;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import guru.springframework.streamingstockquoteservice.model.Quote;
import guru.springframework.streamingstockquoteservice.service.QuoteGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class QuoteHandler {
	
	private final QuoteGeneratorService quoteGeneratorService;
	
	public Mono<ServerResponse> fetchQuotes(ServerRequest request){
		int size = Integer.parseInt(request.queryParam("size").orElse("10"));
		
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(this.quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L))
						.take(size),Quote.class);
	}
	
	public Mono<ServerResponse> streamQuotes(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_NDJSON)
				.body(this.quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200L)),Quote.class);
	}

}
