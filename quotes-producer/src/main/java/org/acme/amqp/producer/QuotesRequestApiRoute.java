package org.acme.amqp.producer;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.ReactiveStreamsNoActiveSubscriptionsException;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import jakarta.ws.rs.core.MediaType;

@RegisterForReflection(targets = {ReactiveStreamsNoActiveSubscriptionsException.class},fields = false, methods = false)
@ApplicationScoped
public class QuotesRequestApiRoute extends RouteBuilder {

    @Inject
    CamelReactiveStreamsService camelReactiveStreamsService;

    @Outgoing("quotes")
    public Publisher<String> getDataFromCamelRoute() {
        return camelReactiveStreamsService.fromStream("quotes", String.class);
    }

    @Override
    public void configure() throws Exception {

        // Stream subscription happens when the browser connects to the SSE
        // Therefore, ignore ReactiveStreamsNoActiveSubscriptionsException
        onException(ReactiveStreamsNoActiveSubscriptionsException.class)
            .handled(true)
        ;
        
        rest("/quotes")
            .post("/request")
                .produces(MediaType.TEXT_PLAIN)
                .to("direct:requestQuote")   
        ;

        from("direct:requestQuote")
            .routeId("request-quote-route")
            .removeHeaders("*")
            .setBody(simple("${uuid}"))
            .log(LoggingLevel.INFO, "Sending request ${body} to 'quote-requests' AMQP queue...")
            .to(ExchangePattern.InOnly, "amqp:queue:quote-requests")
            .log(LoggingLevel.INFO, "Request ${body} sent to 'quote-requests' AMQP queue. DONE.")
        ;

        from("amqp:queue:quotes?acknowledgementModeName=CLIENT_ACKNOWLEDGE") 
            .routeId("consume-quotes-route")
            .log(LoggingLevel.INFO, "Received quote from the 'quotes' AMQP queue: ${body}")
            .to("reactive-streams:quotes")
        ;
    }
    
}
