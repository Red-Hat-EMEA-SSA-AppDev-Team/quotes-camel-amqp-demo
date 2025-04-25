package org.acme.amqp.processor;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class QuoteProcessor extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        fromV("amqp:queue:quote-requests", "requestId")
            .routeId("consume-quote-requests-route")
            .log(LoggingLevel.INFO, "Received quote request ${variable.requestId} from the 'quote-requests' AMQP queue.")
            .delay(200) // simulate some hard-working task
            .setBody().method("quoteHelper", "createQuote(${variable.requestId}, ${random(100)})") // Generate a quote
            .marshal().json(JsonLibrary.Jackson) // Convert the Quote object to JSON
            .log(LoggingLevel.INFO, "Sending quote for request ${variable.requestId} to the 'quotes' AMQP queue: ${body}")
            .to("amqp:queue:quotes")
            .log(LoggingLevel.INFO, "Quote for request ${variable.requestId} sent to 'quotes' AMQP queue. DONE.")
        ;
    }
    
}
