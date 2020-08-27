package com.matthem.webfluxstockquoteservice.service;

import com.matthem.webfluxstockquoteservice.model.Quote;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface QuoteGeneratorService {
  Flux<Quote> fetchQuoteStream(Duration period);
}
