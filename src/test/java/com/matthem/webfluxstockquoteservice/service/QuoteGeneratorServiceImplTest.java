package com.matthem.webfluxstockquoteservice.service;

import com.matthem.webfluxstockquoteservice.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

class QuoteGeneratorServiceImplTest {
  QuoteGeneratorServiceImpl quoteGeneratorService = new QuoteGeneratorServiceImpl();

  @BeforeEach
  void setUp() {
  }

  @Test
  void fetchQuoteStream() {
    Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(1L));
    quoteFlux.take(22000)
            .subscribe(System.out::println);
  }

  @Test
  void fetchQuoteStreamCountDown() throws InterruptedException {
    Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));

    CountDownLatch countDownLatch = new CountDownLatch(1);

    quoteFlux.take(30)
          .subscribe(
              System.out::println,
              e -> System.out.println("Some error occurred"),
              countDownLatch::countDown
          );

    countDownLatch.await();
  }
}