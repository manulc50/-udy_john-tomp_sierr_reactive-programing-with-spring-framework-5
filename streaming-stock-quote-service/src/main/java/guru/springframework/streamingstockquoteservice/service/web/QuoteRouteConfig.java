package guru.springframework.streamingstockquoteservice.service.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class QuoteRouteConfig {
	
	private static final String QUOTES_PATH = "/quotes";
	
	@Bean
	public RouterFunction<ServerResponse> route(QuoteHandler handler){
		return RouterFunctions.route(RequestPredicates.GET(QUOTES_PATH)
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::fetchQuotes) // Versión simplificada de la expresión "request -> handler.fetchQuotes(request)"
				.andRoute(RequestPredicates.GET(QUOTES_PATH)
				.and(RequestPredicates.accept(MediaType.APPLICATION_NDJSON)), handler::streamQuotes); // Versión simplificada de la expresión "request -> handler.streamQuotes(request)"
	}

}
