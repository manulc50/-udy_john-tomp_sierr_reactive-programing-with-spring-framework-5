package guru.springframework.streamingstockquoteservice.service;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guru.springframework.streamingstockquoteservice.model.Quote;
import reactor.core.publisher.Flux;

public class QuoteGeneratorServiceImplTest {
	
	QuoteGeneratorServiceImpl quoteGeneratorService;
	
	@BeforeEach
	void setUp() {
		quoteGeneratorService = new QuoteGeneratorServiceImpl();
	}

	@Test
    void fetchQuoteStream() throws Exception {

        //get quoteFlux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(1L));

        quoteFlux.take(22000)
                .subscribe(System.out::println);

    }
	
	@Test
    void fetchQuoteStreamCountDown() throws Exception {

        //get quoteFlux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));

        //subscriber lambda
        Consumer<Quote> println = System.out::println;

        //error handler
        Consumer<Throwable> errorHandler = e -> System.out.println("Some Error Occurred");

        //set Countdown latch to 1
        CountDownLatch countDownLatch = new CountDownLatch(1);

        //runnable called upon complete, count down latch
        Runnable allDone = () -> countDownLatch.countDown();

        quoteFlux.take(30)
                .subscribe(println, errorHandler, allDone);

        countDownLatch.await();
    }

}
