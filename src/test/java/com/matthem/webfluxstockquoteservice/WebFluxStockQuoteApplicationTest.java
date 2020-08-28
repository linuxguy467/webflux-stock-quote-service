package com.matthem.webfluxstockquoteservice;

import com.matthem.webfluxstockquoteservice.model.Quote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebFluxStockQuoteApplicationTest {
  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testFetchQuotes() {
    webTestClient
        .get()
        .uri("/quotes?size=20")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Quote.class)
        .hasSize(20)
        .consumeWith(allQuotes -> {
          assertThat(allQuotes.getResponseBody())
              .allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());

          assertThat(allQuotes.getResponseBody()).hasSize(20);
        });
  }

  @Test
  public void testStreamQuotes() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(10);

    webTestClient
        .get()
        .uri("/quotes")
        .accept(MediaType.APPLICATION_NDJSON)
        .exchange()
        .returnResult(Quote.class)
        .getResponseBody()
        .take(10)
        .subscribe(quote -> {
          assertThat(quote.getPrice()).isPositive();

          countDownLatch.countDown();
        });

    countDownLatch.await();

    System.out.println("Test Complete");
  }
}
