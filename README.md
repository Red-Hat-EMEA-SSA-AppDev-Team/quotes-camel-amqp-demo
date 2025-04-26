Camel Quarkus AMQP 1.0 Demo
============================

This project demonstrates how to interact with AMQP 1.0 using [Red Hat AMQ Broker](https://docs.redhat.com/en/documentation/red_hat_amq_broker) and [Red Hat build of Apache Camel for Quarkus](https://docs.redhat.com/en/documentation/red_hat_build_of_apache_camel/4.8#Red%20Hat%20build%20of%20Apache%20Camel%20for%20Quarkus).
It builds on the [Quarkus AMQP quickstart](https://quarkus.io/guides/amqp), with both the quote producer and processor reimplemented using Camel for Quarkus.

![](./doc_images/quote-app.png)


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

### Pre-requisites

- [Red Hat AMQ Broker v7.12+](https://docs.redhat.com/en/documentation/red_hat_amq_broker/7.12) operator is deployed in OpenShift

### Instructions

1. Login to the OpenShift cluster:
    ```bash
    oc login ...
    ```
2. Switch to the target OpenShift project:
    ```bash
    oc project ...
    ```
3. Deploy the Red Hat AMQ Broker v7.12+ instance:
4. Switch to the target OpenShift project:
    ```bash
    oc apply -f openshift/amq-broker.yaml
    ```
5. Deploy the `quotes-producer`:
    ```bash
    mvn -f quotes-producer package -Dquarkus.openshift.deploy=true
    ```
6. Deploy the `quotes-processor`:
    ```bash
    mvn -f quotes-processor package -Dquarkus.openshift.deploy=true
    ```
7. Run the following command line for the quotes app frontend URL:
    ```bash
    echo "http://`(oc get route quotes-producer -o jsonpath='{.spec.host}')`/quotes.html"
    ```