Camel Quarkus AMQP 1.0 Demo
============================

This project illustrates how you can interact with AMQP 1.0 (Apache Artemis in this demo) using Red Hat build of Apache Camel.

## Start the application in dev mode

> NOTE: When the `quarkus-messaging-amqp` extension is present and no AMQP broker is explicitly configured, Quarkus automatically enables AMQP Dev Service. This starts a temporary Apache ActiveMQ Artemis broker in development and test modes, streamlining setup and reducing manual configuration.

In a first terminal, run:

```bash
> mvn -f quotes-producer quarkus:dev
```

In a second terminal, run:

```bash
> mvn -f quotes-processor quarkus:dev -Dquarkus.http.port=8081
```  

Then, open your browser to `http://localhost:8080/quotes.html`, and click on the "Request Quote" button.

## Deploy to OpenShift

mvn -f quotes-producer package -Dquarkus.openshift.deploy=true
mvn -f quotes-processor package -Dquarkus.openshift.deploy=true