package com.matthem.webfluxstockquoteservice.service;

import com.matthem.webfluxstockquoteservice.model.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService {
  private final MathContext mathContext = new MathContext(2);
  private final Random random = new Random();
  private final List<Quote> prices = new ArrayList<>();

  public QuoteGeneratorServiceImpl() {
    prices.add(new Quote("AAPL", 160.16));
    prices.add(new Quote("MSFT", 77.74));
    prices.add(new Quote("GOOG", 847.24));
    prices.add(new Quote("ORCL", 49.51));
    prices.add(new Quote("IBM", 159.34));
    prices.add(new Quote("INTC", 39.29));
    prices.add(new Quote("RHT", 84.29));
    prices.add(new Quote("VMW", 92.21));
  }

  @Override
  public Flux<Quote> fetchQuoteStream(Duration period) {
    return Flux.generate(() -> 0,
        (BiFunction<Integer, SynchronousSink<Quote>, Integer>) (index, sink) -> {
          Quote updatedQuote = updateQuote(prices.get(index));
          sink.next(updatedQuote);
          return ++index % prices.size();
        })
        .zipWith(Flux.interval(period))
        .map(Tuple2::getT1)
        .map(quote -> {
          quote.setInstant(Instant.now());
          return quote;
        })
        .log("com.matthem.service.QuoteGenerator");
  }

  private Quote updateQuote(Quote quote) {
    BigDecimal priceChange = quote.getPrice()
        .multiply(BigDecimal.valueOf(0.05 * random.nextDouble()), mathContext);
    return new Quote(quote.getTicker(), quote.getPrice().add(priceChange));
  }
}
