package org.acme.amqp.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.acme.amqp.model.Quote;

@ApplicationScoped
@Named("quoteHelper")
@RegisterForReflection
public class QuoteHelper {
    
    public Quote createQuote(String id, int price) {
        return new Quote(id, price);
    }

}
