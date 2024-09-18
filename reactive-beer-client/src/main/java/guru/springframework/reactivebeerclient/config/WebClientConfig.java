package guru.springframework.reactivebeerclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.logging.LogLevel;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {
	
	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.baseUrl(WebClientProperties.BASE_URL)
				// Para que se muestre por consola los logs de las peticiones http que realiza este cliente
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create()
						//.wiretap(true) // Usa el formato de salida por defecto de los logs
						.wiretap("reactor.netty.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL) // Cambia el formato de salida por defecto de los logs al formato "AdvancedByteBufFormat.TEXTUAL" 
						))
				.build();
	}

}
