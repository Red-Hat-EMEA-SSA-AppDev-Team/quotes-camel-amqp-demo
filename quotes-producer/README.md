# quotes-producer

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw clean compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev-ui.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw clean package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw clean package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

According to your environment, you may want to customize:
- The **AMQP broker connection parameters** by adding the following run-time _system properties_:
    - `quarkus.qpid-jms.url`
    - `quarkus.qpid-jms.username`
    - `quarkus.qpid-jms.password`
- The **OpenTelemetry collector endpoint** by adding the following run-time _system properties_:
    - `quarkus.otel.exporter.otlp.endpoint`

Example:
```shell
java -Dquarkus.qpid-jms.url="amqps://<AMQ_BROKER_HOSTNAME>:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -Dquarkus.otel.exporter.otlp.endpoint="http://localhost:4317" -jar target/quarkus-app/quarkus-run.jar
```

## Creating a native executable

You can create a native executable using the following command:

```shell
./mvnw clean package -Pnative -Dquarkus.native.native-image-xmx=7g
```

>**NOTE** : The project is configured to use a container runtime for native builds. See `quarkus.native.container-build=true` in the [`application.properties`](./src/main/resources/application.properties). Also, adjust the `quarkus.native.native-image-xmx` value according to your container runtime available memory resources.

You can then execute your native executable with: `./target/quotes-producer-1.0.0-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

>**NOTE** : If you are on Apple Silicon and built the native image inside a Linux container (-Dquarkus.native.container-build=true), the result is a Linux ELF binary. macOS can’t execute Linux binaries, so launching it on macOS yields “exec format error”. Follow the steps below to run your Linux native binary.

1. Build the container image of your Linux native binary:
    ```shell
    podman build -f src/main/docker/Dockerfile.native -t quotes-producer .
    ```
2. Run the container:
    ```shell
    podman run --rm --name quotes-producer \
    -p 8080:8080,9876:9876 \
    -e QUARKUS_QPID-JMS_URL="amqps://<AMQ_BROKER_HOSTNAME>:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" \
    -e QUARKUS_QPID-JMS_USERNAME=quotes-producer \
    -e QUARKUS_QPID-JMS_PASSWORD=openshift \
    -e QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://host.containers.internal:4317 \
    quotes-producer 
    ```

## Start-up time comparison in the same environment

Used environment:
- **Laptop**: MacBook PRO
- **CPU**: Apple M2 PRO
- **RAM**: 32Gb
- **Container runtime for native builds**: podman v5.8.2
- **AMQ Broker**: v7.14 running on a remote OpenShift v4.22 SNO cluster

### JVM mode -> _started in **2.007s**_

```shell
# java -Dquarkus.qpid-jms.url="amqps://<AMQ_BROKER_HOSTNAME>:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -Dquarkus.qpid-jms.username=quotes-producer -Dquarkus.qpid-jms.password=openshift -Dquarkus.otel.exporter.otlp.endpoint="http://localhost:4317" -jar target/quarkus-app/quarkus-run.jar
[...]
2026-07-22 20:35:34,954 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.qu.co.CamelBootstrapRecorder] (main) Apache Camel Quarkus 3.33.0.redhat-00008 is starting
2026-07-22 20:35:34,956 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.MainSupport] (main) Apache Camel (Main) 4.18.1.redhat-00026 is starting
2026-07-22 20:35:34,990 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main) Auto-configuration summary
2026-07-22 20:35:34,991 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main)     [MicroProfilePropertiesSource] camel.context.name = quotes-producer
2026-07-22 20:35:35,147 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.18.1.redhat-00026 (quotes-producer) is starting
2026-07-22 20:35:35,223 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.op.OpenTelemetryTracer] (main) Opentelemetry2 enabled
2026-07-22 20:35:35,836 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.qp.jm.JmsConnection] (AmqpProvider :(1):[amqps://<AMQ_BROKER_HOSTNAME>:443]) Connection ID:4524cdc9-d880-4074-955f-48b8d86e0b63:1 connected to server: amqps://<AMQ_BROKER_HOSTNAME>:443
2026-07-22 20:35:35,843 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Routes startup (total:2 rest-dsl:1)
2026-07-22 20:35:35,844 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started consume-quotes-route (amqp://queue:quotes)
2026-07-22 20:35:35,844 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started request-quote-route (rest://post:/quotes:/request)
2026-07-22 20:35:35,844 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.18.1.redhat-00026 (quotes-producer) started in 696ms (build:0ms init:0ms start:696ms boot:805ms)
2026-07-22 20:35:35,912 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) quotes-producer 1.0.0 on JVM (powered by Quarkus 3.33.2.redhat-00004) started in 2.007s. Listening on: http://0.0.0.0:8080. Management interface listening on http://0.0.0.0:9876.
2026-07-22 20:35:35,912 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Profile prod activated. 
2026-07-22 20:35:35,912 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Installed features: [camel-amqp, camel-attachments, camel-bean, camel-core, camel-direct, camel-jackson, camel-jms, camel-jolokia, camel-management, camel-micrometer, camel-microprofile-health, camel-observability-services, camel-opentelemetry2, camel-platform-http, camel-reactive-streams, camel-rest, camel-smallrye-reactive-messaging, camel-xml-io-dsl, cdi, kubernetes, messaging, messaginghub-pooled-jms, micrometer, opentelemetry, qpid-jms, rest, smallrye-context-propagation, smallrye-health, vertx]
```

### Native mode -> _started in **0.508s**_

```shell
# podman run --rm --name quotes-producer -p 8081:8080,9876:9876 -e QUARKUS_QPID-JMS_URL="amqps://<AMQ_BROKER_HOSTNAME>:443?transport.trustAll=true&transport.verifyHost=false&amqp.idleTimeout=120000" -e QUARKUS_QPID-JMS_USERNAME=quotes-producer -e QUARKUS_QPID-JMS_PASSWORD=openshift -e QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://host.containers.internal:4317 quotes-producer
[...]
2026-07-22 18:36:33,758 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.qu.co.CamelBootstrapRecorder] (main) Apache Camel Quarkus 3.33.0.redhat-00008 is starting
2026-07-22 18:36:33,759 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.MainSupport] (main) Apache Camel (Main) 4.18.1.redhat-00026 is starting
2026-07-22 18:36:33,776 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main) Auto-configuration summary
2026-07-22 18:36:33,776 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.ma.BaseMainSupport] (main)     [MicroProfilePropertiesSource] camel.context.name = quotes-producer
2026-07-22 18:36:33,795 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.18.1.redhat-00026 (quotes-producer) is starting
2026-07-22 18:36:33,835 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.op.OpenTelemetryTracer] (main) Opentelemetry2 enabled
2026-07-22 18:36:34,143 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.qp.jm.JmsConnection] (AmqpProvider :(1):[amqps://<AMQ_BROKER_HOSTNAME>:443]) Connection ID:d41eab63-8214-40fc-ac22-7d22f949c0a6:1 connected to server: amqps://<AMQ_BROKER_HOSTNAME>:443
2026-07-22 18:36:34,144 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Routes startup (total:2 rest-dsl:1)
2026-07-22 18:36:34,144 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started consume-quotes-route (amqp://queue:quotes)
2026-07-22 18:36:34,144 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main)     Started request-quote-route (rest://post:/quotes:/request)
2026-07-22 18:36:34,144 INFO  traceId=, parentId=, spanId=, sampled= [or.ap.ca.im.en.AbstractCamelContext] (main) Apache Camel 4.18.1.redhat-00026 (quotes-producer) started in 348ms (build:0ms init:0ms start:348ms)
2026-07-22 18:36:34,146 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) quotes-producer 1.0.0 native (powered by Quarkus 3.33.2.redhat-00004) started in 0.508s. Listening on: http://0.0.0.0:8080. Management interface listening on http://0.0.0.0:9876.
2026-07-22 18:36:34,146 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Profile prod activated. 
2026-07-22 18:36:34,146 INFO  traceId=, parentId=, spanId=, sampled= [io.quarkus] (main) Installed features: [camel-amqp, camel-attachments, camel-bean, camel-core, camel-direct, camel-jackson, camel-jms, camel-jolokia, camel-management, camel-micrometer, camel-microprofile-health, camel-observability-services, camel-opentelemetry2, camel-platform-http, camel-reactive-streams, camel-rest, camel-smallrye-reactive-messaging, camel-xml-io-dsl, cdi, kubernetes, messaging, messaginghub-pooled-jms, micrometer, opentelemetry, qpid-jms, rest, smallrye-context-propagation, smallrye-health, vertx]
```