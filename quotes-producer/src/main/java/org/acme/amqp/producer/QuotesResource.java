package org.acme.amqp.producer;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;

@Path("/quotes")
public class QuotesResource {

    @Inject
    @Channel("quotes")
    Multi<String> quote;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> streamQuotes() {
        return quote;
    }
    
}
